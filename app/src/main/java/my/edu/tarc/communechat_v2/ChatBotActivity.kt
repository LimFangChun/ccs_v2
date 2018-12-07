package my.edu.tarc.communechat_v2

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat_bot.*
import my.edu.tarc.communechat_v2.Adapter.ChatRoomRecyclerAdapter
import my.edu.tarc.communechat_v2.MainActivity.mqttHelper
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.internal.MqttHelper
import my.edu.tarc.communechat_v2.model.Message
import my.edu.tarc.communechat_v2.model.User
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*

class ChatBotActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences
    private val messageArrayList = ArrayList<Message>()
    private lateinit var chatRoomRecyclerAdapter: ChatRoomRecyclerAdapter
    private val layoutManager = LinearLayoutManager(this@ChatBotActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        chatRoomRecyclerAdapter = ChatRoomRecyclerAdapter(this@ChatBotActivity, messageArrayList, "ChatBot")

        title = "Chat Bot (FAQ)"
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.subtitle = "DialogFlow by Google"

        initChatBot()
        initButtonListener()
    }

    private fun initChatBot() {
        val message = Message()
        message.message_id = 0
        message.room_id = 0
        message.message = "Hello, I'm DialogFlow. What can I help you today?"
        message.message_type = "Text"
        message.sender_id = 0
        message.sender_name = "DialogFlow"
        messageArrayList.add(message)

        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView_chatBot.layoutManager = layoutManager
        recyclerView_chatBot.adapter = chatRoomRecyclerAdapter
        recyclerView_chatBot.smoothScrollToPosition(chatRoomRecyclerAdapter.getLastIndex())
    }

    private fun initButtonListener() {
        button_sendBot.setOnClickListener {
            //check for empty input
            if (editText_messageBot.text.isEmpty()) {
                editText_messageBot.error = "Field can't be empty"
                return@setOnClickListener
            }

            val header = MqttHeader.CHAT_BOT

            val message = Message()
            message.sender_id = pref.getInt(User.COL_USER_ID, -1)
            message.date_created = Calendar.getInstance()
            message.message = editText_messageBot.text.toString().trim()
            message.room_id = 0
            message.message_type = ChatRoomActivity.TEXT
            message.sender_name = pref.getString(User.COL_DISPLAY_NAME, "")

            val topic = "$header/${message.sender_id}"
            mqttHelper!!.connectPublishSubscribe(this@ChatBotActivity, topic, header, message)
            mqttHelper.mqttClient.setCallback(chatBotCallback)
            editText_messageBot.text.clear()
            messageArrayList.add(message)
            chatRoomRecyclerAdapter.notifyItemInserted(messageArrayList.size - 1)
            recyclerView_chatBot.smoothScrollToPosition(chatRoomRecyclerAdapter.getLastIndex())
        }
    }

    private val chatBotCallback = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            val helper = MqttHelper()
            helper.decode(message.toString())
            if (helper.receivedHeader == MqttHeader.CHAT_BOT_REPLY) {
                val result = Message()
                result.sender_id = 0
                result.date_created = Calendar.getInstance()
                result.message = helper.receivedResult
                result.room_id = 0
                result.message_type = ChatRoomActivity.TEXT
                result.sender_name = "DialogFlow"

                messageArrayList.add(result)
                chatRoomRecyclerAdapter.notifyItemInserted(messageArrayList.size - 1)
                recyclerView_chatBot.smoothScrollToPosition(chatRoomRecyclerAdapter.getLastIndex())
                mqttHelper.unsubscribe(topic)
            }
        }

        override fun connectionLost(cause: Throwable?) {
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
        }
    }
}
