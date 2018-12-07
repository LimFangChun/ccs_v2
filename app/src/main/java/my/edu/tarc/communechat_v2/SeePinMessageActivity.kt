package my.edu.tarc.communechat_v2

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_see_pin_message.*
import my.edu.tarc.communechat_v2.Adapter.ChatRoomRecyclerAdapter
import my.edu.tarc.communechat_v2.Utility.MyUtil
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.internal.MqttHelper
import my.edu.tarc.communechat_v2.model.Chat_Room
import my.edu.tarc.communechat_v2.model.Message
import my.edu.tarc.communechat_v2.model.Participant
import my.edu.tarc.communechat_v2.model.User
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONArray

class SeePinMessageActivity : AppCompatActivity() {

    companion object {
        private lateinit var pref: SharedPreferences
        private val chatRoom = Chat_Room()
        private val messageArrayList = ArrayList<Message>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_pin_message)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        initializeChatRoomByRoomID()
    }

    private fun initializeChatRoomByRoomID() {
        //show progress bar
        progressBar_pin.visibility = View.VISIBLE

        chatRoom.room_id = intent.getIntExtra(Chat_Room.COL_ROOM_ID, -1)
        chatRoom.room_name = intent.getStringExtra(Chat_Room.COL_ROOM_NAME)
        chatRoom.role = intent.getStringExtra(Participant.COL_ROLE)

        if ("" != chatRoom.room_name) {
            title = "Pinned messages"
            supportActionBar?.subtitle = chatRoom.room_name
        }

        //chatRoom.setSecret_key(pref.getString(RoomSecretHelper.getRoomPrefKey(chatRoom.getRoom_id()),null).getBytes());
        val header = MqttHeader.GET_PINNED_MESSAGE
        val topic = "$header/room" + chatRoom.room_id + "_user" + pref.getInt(User.COL_USER_ID, -1)

        MainActivity.mqttHelper.connectPublishSubscribe(this, topic, header, chatRoom)
        MainActivity.mqttHelper.mqttClient.setCallback(getRoomMessagesCallback)
    }

    private val getRoomMessagesCallback = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            //initialize room messages
            val helper = MqttHelper()
            helper.decode(message.toString())
            if (helper.receivedResult == MqttHeader.NO_RESULT) {
                val alertDialog = AlertDialog.Builder(this@SeePinMessageActivity)
                alertDialog.setTitle(R.string.notice)
                alertDialog.setMessage("This chat room doesn't have any pinned message yet")
                alertDialog.setPositiveButton(R.string.ok, null)
                alertDialog.setOnDismissListener {
                    finish()
                }
                alertDialog.show()
            } else if (helper.receivedHeader == MqttHeader.GET_PINNED_MESSAGE_REPLY) {
                MainActivity.mqttHelper.unsubscribe(topic)
                processReceivedMessages(helper.receivedResult)
            }
        }

        override fun connectionLost(cause: Throwable?) {
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
        }
    }

    private fun processReceivedMessages(receivedResult: String) {
        val chatRoomRecyclerAdapter = ChatRoomRecyclerAdapter(this@SeePinMessageActivity, messageArrayList, chatRoom.role)
        val layoutManager = LinearLayoutManager(this@SeePinMessageActivity)
        try {
            val jsonRoomMessage = JSONArray(receivedResult)

            //put all received messages into array list
            //and put into adapter later
            for (x in 0 until jsonRoomMessage.length()) {
                val receivedMessage = jsonRoomMessage.getJSONObject(x)

                //put into message object
                val message = Message()
                message.message_id = receivedMessage.getInt(Message.COL_MESSAGE_ID)
                message.room_id = receivedMessage.getInt(Message.COL_ROOM_ID)
                message.message = receivedMessage.getString(Message.COL_MESSAGE) //todo decrypt here
                message.message_type = receivedMessage.getString(Message.COL_MESSAGE_TYPE)
                message.setDate_created(receivedMessage.getString(Message.COL_DATE_CREATED))
                message.sender_id = receivedMessage.getInt(Message.COL_SENDER_ID)
                message.sender_name = receivedMessage.getString(User.COL_DISPLAY_NAME)
                message.status = receivedMessage.getString(Message.COL_STATUS)

                messageArrayList.add(message)
            }

            //add an extra item that indicate remaining messages are today
            for (x in messageArrayList.size - 1 downTo 0) {
                if (!MyUtil.isToday(messageArrayList[x].date_created)) {
                    if (x == messageArrayList.size - 1) {
                        break
                    }
                    val message = Message()
                    message.message_type = ChatRoomRecyclerAdapter.DATE
                    messageArrayList.add(x + 1, message)
                    break
                }
            }

            //layout manager indicate how the recycler view should display, such as grid view, linear vertical etc
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            recyclerView_pin.layoutManager = layoutManager

            //put the adapter to recycler view
            recyclerView_pin.adapter = chatRoomRecyclerAdapter

            //scroll to last item
            recyclerView_pin.scrollToPosition(chatRoomRecyclerAdapter.getLastIndex())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        progressBar_pin.visibility = ProgressBar.GONE
    }
}
