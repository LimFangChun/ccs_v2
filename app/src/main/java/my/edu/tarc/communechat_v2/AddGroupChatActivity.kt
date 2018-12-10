package my.edu.tarc.communechat_v2

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_add_group_chat.*
import my.edu.tarc.communechat_v2.MainActivity.mqttHelper
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.internal.MqttHelper
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper
import my.edu.tarc.communechat_v2.model.Chat_Room
import my.edu.tarc.communechat_v2.model.Participant
import my.edu.tarc.communechat_v2.model.Student
import my.edu.tarc.communechat_v2.model.User
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AddGroupChatActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences
    private var userID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group_chat)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        userID = pref.getInt(User.COL_USER_ID, -1)

        progressBar_add.visibility = View.VISIBLE

        initListView()
        initListeners()
    }

    private fun initListeners() {
        button_add.setOnClickListener {
            if (editText_roomName.text.isEmpty()) {
                editText_roomName.error = "Please enter the room type as well"
                return@setOnClickListener
            }
            addGroupChat()
        }
    }

    private fun addGroupChat() {
        progressBar_add.visibility = View.VISIBLE
        val roomName = editText_roomName.text.toString()
        val sparseBooleanArray = listView_friendList.checkedItemPositions
        val jsonArray = JSONArray()
        for (x in 0 until listView_friendList.count) {
            if (sparseBooleanArray.get(x)) {
                val jsonObject = JSONObject()
                val temp = listView_friendList.getItemAtPosition(x).toString().split(".")
                jsonObject.put(User.COL_USER_ID, temp[0])
                jsonArray.put(jsonObject)
            }
        }

        val topic = mqttHelper.topicPrefix + "createPublicChatRoom/$userID"
        val header = MqttHeader.CREATE_PUBLIC_CHAT_ROOM

        val message = "$header,$userID,$roomName,$jsonArray"
        val mqttMessage = MqttMessage(message.toByteArray())
        var mqttAndroidClient = mqttHelper.mqttClient
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected) {
            mqttAndroidClient = MqttAndroidClient(this, mqttHelper.mqttClient.serverURI, mqttHelper.mqttClient.clientId)
            try {
                val token = mqttAndroidClient.connect()
                token.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        mqttAndroidClient.publish(topic, mqttMessage)
                        mqttAndroidClient.subscribe(topic, 1)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        Log.i("MqttHelper", mqttAndroidClient.clientId + " failed to connect. " + exception)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            mqttAndroidClient.publish(topic, mqttMessage)
            mqttAndroidClient.subscribe(topic, 1)
        }

        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d("AddGroup", message.toString())
                val helper = MqttHelper()
                helper.decode(message.toString())
                if (helper.receivedHeader == MqttHeader.CREATE_PUBLIC_CHAT_ROOM_REPLY) {
                    if (helper.receivedResult == MqttHeader.NO_RESULT) {
                        val builder = AlertDialog.Builder(this@AddGroupChatActivity)
                        builder.setTitle(R.string.failed)
                        builder.setMessage("Error occurred when creating group chat")
                        builder.setPositiveButton(R.string.ok, null)
                        builder.show()
                    } else {

                        val chat_room = Chat_Room()
                        chat_room.room_id = helper.receivedResult.toInt()
                        RoomSecretHelper.sendRoomSecret(applicationContext, chat_room)

                        val intent = Intent()
                        intent.putExtra(Chat_Room.COL_ROOM_ID, helper.receivedResult.toInt())
                        intent.putExtra(Chat_Room.COL_ROOM_TYPE, "Public")
                        intent.putExtra(Participant.COL_ROLE, "Admin")
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    progressBar_add.visibility = View.GONE
                    mqttHelper.unsubscribe(topic)
                }
            }

            override fun connectionLost(cause: Throwable?) {
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })
    }

    private fun initListView() {
        progressBar_add.visibility = View.VISIBLE
        val topic = mqttHelper.topicPrefix + "getFriendList/" + pref.getInt(User.COL_USER_ID, -1)
        val header = MqttHeader.GET_FRIEND_LIST
        val user = User()
        user.user_id = userID
        mqttHelper.connectPublishSubscribe(this, topic, header, user)
        mqttHelper.mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val helper = MqttHelper()
                helper.decode(message.toString())
                if (helper.receivedHeader == MqttHeader.GET_FRIEND_LIST_REPLY) {
                    if (helper.receivedResult == MqttHeader.NO_RESULT) {
                        val result = ArrayList<String>()
                        result.add("Seems like you don't have any friend yet")
                        val adapter = ArrayAdapter(this@AddGroupChatActivity, android.R.layout.simple_list_item_1, result)
                        listView_friendList.adapter = adapter
                    } else {
                        try {
                            val result = JSONArray(helper.receivedResult)

                            val friendList = arrayListOf<String>()
                            for (i in 0 until result.length()) {
                                val friend = Student()
                                val temp = result.getJSONObject(i)
                                friend.user_id = temp.getInt(User.COL_USER_ID)
                                friend.display_name = temp.getString(Student.COL_DISPLAY_NAME)
                                friend.status = temp.getString(Student.COL_STATUS)
                                friend.setLast_online(temp.getString(Student.COL_LAST_ONLINE))
                                friend.course = temp.getString(Student.COL_COURSE)
                                friend.academic_year = temp.getInt(Student.COL_ACADEMIC_YEAR)
                                friend.tutorial_group = temp.getInt(Student.COL_TUTORIAL_GROUP)

                                friendList.add("${friend.user_id}. ${friend.display_name}")
                            }
                            val adapter = ArrayAdapter<String>(this@AddGroupChatActivity,
                                    android.R.layout.simple_list_item_multiple_choice, friendList)
                            listView_friendList.adapter = adapter
                            listView_friendList.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        }

                    }
                    progressBar_add.visibility = View.GONE
                    mqttHelper.unsubscribe(topic)
                }
            }

            override fun connectionLost(cause: Throwable?) {
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })
    }
}
