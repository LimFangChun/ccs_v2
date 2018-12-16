package my.edu.tarc.communechat_v2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat_room.*
import my.edu.tarc.communechat_v2.Adapter.ChatRoomRecyclerAdapter
import my.edu.tarc.communechat_v2.MainActivity.mqttHelper
import my.edu.tarc.communechat_v2.Utility.CompressImageAsync
import my.edu.tarc.communechat_v2.Utility.MyUtil
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.internal.MqttHelper
import my.edu.tarc.communechat_v2.internal.PrivacyControl
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper
import my.edu.tarc.communechat_v2.model.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ChatRoomActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG = "ChatRoomActivity"

        const val REQUEST_CAMERA = 1
        const val REQUEST_GALLERY = 2

        const val ACTION = "Action"
        const val IMAGE = "Image"
        const val TEXT = "Text"
        const val WARNING = "Warning"
    }

    private val messageArrayList = ArrayList<Message>()
    private lateinit var chatRoomRecyclerAdapter: ChatRoomRecyclerAdapter
    private val layoutManager = LinearLayoutManager(this@ChatRoomActivity)

    private var pref: SharedPreferences? = null
    private var chatRoom: Chat_Room? = null
    private var chatMqttHelper: MqttHelper? = null
    private var topic: String? = null

    private var interval = true

    private//method to check internet connection
    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo: NetworkInfo?
            activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        when {
            chatRoom!!.room_type == "Private" -> menuInflater.inflate(R.menu.room_private_menu, menu)
            chatRoom!!.role == "Admin" -> menuInflater.inflate(R.menu.room_admin_menu, menu)
            else -> menuInflater.inflate(R.menu.room_member_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        val intent: Intent
        when (itemId) {
            R.id.nav_add_people -> {
                intent = Intent(this@ChatRoomActivity, AddPeopleToChatActivity::class.java)
                intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom!!.room_id)
                startActivity(intent)
            }
            R.id.nav_remove_people -> {
                intent = Intent(this@ChatRoomActivity, RemovePeopleFromChatActivity::class.java)
                intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom!!.room_id)
                startActivity(intent)
            }
            R.id.nav_exit_group -> exitGroup()
            R.id.nav_group_info -> {
                intent = Intent(this@ChatRoomActivity, GroupInfoActivity::class.java)
                intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom!!.room_id)
                startActivity(intent)
            }
            R.id.nav_see_pin -> {
                intent = Intent(this@ChatRoomActivity, SeePinMessageActivity::class.java)
                intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom!!.room_id)
                intent.putExtra(Chat_Room.COL_ROOM_NAME, chatRoom!!.room_name)
                intent.putExtra(Participant.COL_ROLE, chatRoom!!.role)
                intent.putExtra(Chat_Room.COL_SECRET_KEY, chatRoom!!.secret_key)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        chatMqttHelper = MqttHelper()

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //init views
        pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        //hide button and text view that use for camera and select images
        button_addImage.visibility = View.GONE
        button_camera.visibility = View.GONE
        textView_addImage.visibility = View.GONE
        textView_camera.visibility = View.GONE

        chatRoom = Chat_Room()
        chatRoom!!.room_type = intent.getStringExtra(Chat_Room.COL_ROOM_TYPE)
        chatRoom!!.role = intent.getStringExtra(Participant.COL_ROLE)
        chatRoom!!.room_id = intent.getIntExtra(Chat_Room.COL_ROOM_ID, -1)
        chatRoomRecyclerAdapter = ChatRoomRecyclerAdapter(this@ChatRoomActivity, messageArrayList, chatRoom!!.role)

        val secretKey = pref!!.getString(RoomSecretHelper.getRoomPrefKey(chatRoom!!.room_id), null)
        if (secretKey == null) {
//            chatViewRoom.getInputEditText().setEnabled(false);
//            chatViewRoom.getInputEditText().setHint("Initializing... please try again later.");
            editText_message.isEnabled = false
            editText_message.setText("Initializing...")
            button_send.isEnabled = false
            val uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
            val user = User()
            user.user_id = pref!!.getInt(User.COL_USER_ID, -1)
            val params = arrayOf(user, chatRoom)
            mqttHelper.connectPublishSubscribe(applicationContext, uniqueTopic, MqttHeader.GET_CHATROOM_SECRET, params)
            mqttHelper.mqttClient.setCallback(getRoomMessagesCallback)

        } else {
            chatRoom!!.secret_key = secretKey
        }

        topic = MqttHeader.SEND_ROOM_MESSAGE + "/room" + chatRoom!!.room_id

        if (isNetworkAvailable) {
            initializeChatRoomByRoomID()
        } else {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show()
        }

        initializeListeners()
    }

    private fun initializeListeners() {
        button_send.setOnClickListener {
            //check for empty input
            if (editText_message.text.isEmpty()) {
                editText_message.error = "Field can't be empty"
                return@setOnClickListener
            }

            val privacyControlEnabled = pref!!.getBoolean("privacyControl", true)
            if (!PrivacyControl(editText_message.text.toString().trim()).verify(pref)) {
                sendMessage()
            } else {
                if (!privacyControlEnabled) {
                    sendMessage()
                }
                //show privacy control message AFTER sending(if any) since this function will also clear editText_message
                showPrivacyControlWarning(privacyControlEnabled)
            }
        }

        parent_layout.setOnClickListener(this)

        button_openTool.setOnClickListener(this)

        button_addImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_GALLERY)
        }

        button_camera.setOnClickListener {
            //todo has bug fix this
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            val file = File(this.externalCacheDir,
                    System.currentTimeMillis().toString() + ".jpg")
            val fileUri = Uri.fromFile(file)
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    private fun sendMessage() {
        val header = MqttHeader.SEND_ROOM_MESSAGE
        val message = Message()
        val messageText = editText_message.text.toString().trim()
        message.sender_id = pref!!.getInt(User.COL_USER_ID, -1)
        message.date_created = Calendar.getInstance()
        message.message = AdvancedEncryptionStandard(chatRoom!!.secret_key).encrypt(messageText)
        message.room_id = chatRoom!!.room_id
        message.message_type = TEXT
        message.sender_name = pref!!.getString(User.COL_DISPLAY_NAME, "")

        chatMqttHelper!!.connectPublish(this@ChatRoomActivity, topic, header, message)
        editText_message.text.clear()

        //get another copy of message to prevent displaying encrypted text
        val displayMessage = message.copy()
        displayMessage.message = messageText
        messageArrayList.add(displayMessage)
        chatRoomRecyclerAdapter.notifyItemInserted(messageArrayList.size - 1)
        recyclerView_chat.smoothScrollToPosition(chatRoomRecyclerAdapter.getLastIndex())
    }

    private fun showPrivacyControlWarning(privacyControlEnabled: Boolean) {
        val message = Message()
        if (privacyControlEnabled) {
            message.message = getString(R.string.privacyControl_msgBlocked)
        } else {
            message.message = getString(R.string.privacyControl_msgSent)
        }
        message.message_type = WARNING
        editText_message.text.clear()
        messageArrayList.add(message)
        chatRoomRecyclerAdapter.notifyItemInserted(messageArrayList.size - 1)
        recyclerView_chat.smoothScrollToPosition(chatRoomRecyclerAdapter.getLastIndex())
    }

    private fun initializeChatRoomByRoomID() {
        //show progress bar
        progressBar_chatRoom!!.visibility = View.VISIBLE

        val chatRoom = Chat_Room()
        chatRoom.room_id = intent.getIntExtra(Chat_Room.COL_ROOM_ID, -1)
        chatRoom.room_name = intent.getStringExtra(Chat_Room.COL_ROOM_NAME)
        this.chatRoom!!.room_name = intent.getStringExtra(Chat_Room.COL_ROOM_NAME)

        if ("" != chatRoom.room_name) {
            title = chatRoom.room_name
        }

        val topic = "getRoomMessage/room" + chatRoom.room_id + "_user" + pref!!.getInt(User.COL_USER_ID, -1)
        val header = MqttHeader.GET_ROOM_MESSAGE
        mqttHelper.connectPublishSubscribe(this, topic, header, chatRoom)
        mqttHelper.connectSubscribe(this, this.topic)
        mqttHelper.mqttClient.setCallback(getRoomMessagesCallback)
    }


    private fun makeVibrationOrSound() {
        if (interval) {
            val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            when (audio.ringerMode) {
                AudioManager.RINGER_MODE_NORMAL -> MyUtil.makeSound(this)
                AudioManager.RINGER_MODE_VIBRATE -> MyUtil.makeVibration(this, MyUtil.VIBRATE_SHORT)
            }
        }

        interval = false

        Handler().postDelayed({ interval = true }, 3000)
    }

    private fun exitGroup() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.exit_group))
        alertDialog.setMessage(R.string.exit_group_desc)
        alertDialog.setNegativeButton(getString(R.string.cancel), null)
        alertDialog.setPositiveButton(getString(R.string.yes)) { _, _ -> confirmedExitGroup() }
        alertDialog.show()
    }

    private fun confirmedExitGroup() {
        val alertDialog = AlertDialog.Builder(this@ChatRoomActivity)

        val topic = "exitGroup/" + pref!!.getInt(User.COL_USER_ID, -1)
        val header = MqttHeader.DELETE_CHAT_ROOM
        val participant = Participant()
        participant.room_id = chatRoom!!.room_id
        participant.user_id = pref!!.getInt(User.COL_USER_ID, -1)
        mqttHelper.connectPublishSubscribe(this, topic, header, participant)
        mqttHelper.mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {

            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                mqttHelper.decode(message.toString())
                if (mqttHelper.receivedHeader == MqttHeader.DELETE_CHAT_ROOM_REPLY) {
                    if (mqttHelper.receivedResult == MqttHeader.SUCCESS) {
                        alertDialog.setTitle(getString(R.string.success))
                        alertDialog.setMessage(R.string.exit_group_success_desc)
                        alertDialog.setNeutralButton(getString(R.string.ok), null)
                        alertDialog.setOnDismissListener { finish() }
                        alertDialog.show()
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        chatMqttHelper!!.disconnect()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("CameraMain", "resultCode: ${resultCode == RESULT_OK} data: $data")
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CAMERA, REQUEST_GALLERY -> {
                    addNewImage(data.data)
                }
            }
        }

    }

    private val getRoomMessagesCallback = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            //initialize room messages
            val helper = MqttHelper()
            helper.decode(message.toString())
            if (helper.receivedResult == MqttHeader.NO_RESULT) {
                return
            } else if (helper.receivedHeader == MqttHeader.GET_ROOM_MESSAGE_REPLY) {
                mqttHelper.unsubscribe(topic)
                initializeRoomMessages(helper.receivedResult)
            } else if (helper.receivedHeader == MqttHeader.SEND_ROOM_MESSAGE ||
                    helper.receivedHeader == MqttHeader.SEND_ROOM_IMAGE) {
                addReceivedMessage(helper.receivedResult)
            } else if (helper.receivedHeader.equals(MqttHeader.GET_CHATROOM_SECRET_REPLY)) {
                //Todo: move this to other place later
                Log.i(TAG, "Saving chatroom secret...")
                mqttHelper.unsubscribe(topic)

                val jsonResult = JSONArray(helper.receivedResult)

                val editor = pref!!.edit()
                editor.putString(RoomSecretHelper.getRoomPrefKey(chatRoom!!.room_id), jsonResult.getJSONObject(0).getString(Chat_Room.COL_SECRET_KEY))
                editor.apply()

                editText_message.isEnabled = true
                editText_message.setText("")
                button_send.isEnabled = true
            }
        }

        override fun connectionLost(cause: Throwable?) {
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
        }
    }

//    private val getRoomSecretCallback = object : MqttCallback {
//        override fun messageArrived(topic: String?, message: MqttMessage?) {
//            val helper = MqttHelper()
//            helper.decode(message.toString())
//            if (helper.receivedHeader.equals(MqttHeader.GET_CHATROOM_SECRET_REPLY) && !helper.receivedResult.equals(MqttHeader.NO_RESULT)) {
//                Log.i(TAG, "Saving chatroom secret...")
//                mqttHelper.unsubscribe(topic)
//
//                val jsonResult = JSONArray(helper.receivedResult)
//
//                val editor = pref!!.edit()
//                editor.putString(RoomSecretHelper.getRoomPrefKey(chatRoom!!.room_id), jsonResult.getJSONObject(0).getString(Chat_Room.COL_SECRET_KEY))
//                editor.commit();
//
//                editText_message.isEnabled = true
//                editText_message.setText("")
//                button_send.isEnabled = true
//            }
//        }
//
//        override fun connectionLost(cause: Throwable?) {
//        }
//
//        override fun deliveryComplete(token: IMqttDeliveryToken?) {
//        }
//
//    }

    private fun initializeRoomMessages(receivedResult: String) {
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
                message.message_type = receivedMessage.getString(Message.COL_MESSAGE_TYPE)
                message.setDate_created(receivedMessage.getString(Message.COL_DATE_CREATED))
                message.sender_id = receivedMessage.getInt(Message.COL_SENDER_ID)
                message.sender_name = receivedMessage.getString(User.COL_DISPLAY_NAME)
                message.status = receivedMessage.getString(Message.COL_STATUS)

                when (message.message_type) {
                    TEXT -> message.message = AdvancedEncryptionStandard(chatRoom!!.secret_key).decrypt(receivedMessage.getString(Message.COL_MESSAGE))
                    else -> message.message = receivedMessage.getString(Message.COL_MESSAGE)
                }
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
            recyclerView_chat.layoutManager = layoutManager

            //put the adapter to recycler view
            recyclerView_chat.adapter = chatRoomRecyclerAdapter

            //scroll to last item
            recyclerView_chat.scrollToPosition(chatRoomRecyclerAdapter.getLastIndex())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        progressBar_chatRoom.visibility = ProgressBar.GONE
    }

    private fun addReceivedMessage(newMessage: String) {
        try {
            val receivedMessage = JSONObject(newMessage)

            //check is received message is sent by current user
            if (isMine(receivedMessage.getInt(Message.COL_SENDER_ID))) {
                return
            }

            makeVibrationOrSound()
            
            //put into message object
            val message = Message()
            message.room_id = receivedMessage.getInt(Message.COL_ROOM_ID)
            message.message_type = receivedMessage.getString(Message.COL_MESSAGE_TYPE)

            if (message.message_type == TEXT) {
                message.message = AdvancedEncryptionStandard(chatRoom!!.secret_key).decrypt(receivedMessage.getString(Message.COL_MESSAGE))
            } else if (message.message_type == IMAGE) {
                //todo test
                message.media = Base64.decode(receivedMessage.getString(Message.COL_MEDIA), Base64.DEFAULT)
            }

            message.setDate_created(receivedMessage.getString(Message.COL_DATE_CREATED))
            message.sender_id = receivedMessage.getInt(Message.COL_SENDER_ID)
            message.sender_name = receivedMessage.getString(User.COL_DISPLAY_NAME)

            chatRoomRecyclerAdapter.addMessage(message)
            //todo test
            if (layoutManager.findLastCompletelyVisibleItemPosition() == chatRoomRecyclerAdapter.itemCount) {
                recyclerView_chat.smoothScrollToPosition(chatRoomRecyclerAdapter.getLastIndex())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNewImage(filePath: Uri) {
        val message = Message()

        message.sender_id = pref!!.getInt(User.COL_USER_ID, -1)
        message.date_created = Calendar.getInstance()
        message.room_id = chatRoom!!.room_id
        message.message_type = IMAGE
        message.sender_name = pref!!.getString(User.COL_DISPLAY_NAME, "")
        message.mediaPath = filePath

        CompressImageAsync(this, topic = topic,
                recyclerView = recyclerView_chat, message = message, messageArrayList = messageArrayList,
                adapter = chatRoomRecyclerAdapter, filePath = filePath)
                .execute()
    }

    private fun isMine(userID: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return pref.getInt(User.COL_USER_ID, -1) == userID
    }

    private var isOpen = false
    override fun onClick(v: View?) {
        //todo got some visual bug no big deal
        when (v!!.id) {
            R.id.button_openTool -> if (!isOpen) {
                isOpen = true
                setToolsVisibility(View.VISIBLE)
            } else {
                isOpen = false
                setToolsVisibility(View.GONE)
            }
            else -> {
                isOpen = false
                setToolsVisibility(View.GONE)
            }
        }
    }

    private fun setToolsVisibility(visibility: Int) {
        //animation cuz we are fabulous LUL
        val animationSet = AnimationSet(true)
        if (visibility == View.VISIBLE) {
            val slideUp = AnimationUtils.loadAnimation(this, R.anim.chat_room_tool_slide_up)
            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            animationSet.addAnimation(slideUp)
            animationSet.addAnimation(fadeIn)
        } else {
            val slideDown = AnimationUtils.loadAnimation(this, R.anim.chat_room_tool_slide_down)
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            animationSet.addAnimation(slideDown)
            animationSet.addAnimation(fadeOut)
        }

        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                button_addImage.visibility = visibility
                button_camera.visibility = visibility
                textView_addImage.visibility = visibility
                textView_camera.visibility = visibility
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })

        button_addImage.startAnimation(animationSet)
        button_camera.startAnimation(animationSet)
        textView_addImage.startAnimation(animationSet)
        textView_camera.startAnimation(animationSet)
    }
}
