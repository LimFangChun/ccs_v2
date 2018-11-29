package my.edu.tarc.communechat_v2.Background

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import my.edu.tarc.communechat_v2.MainActivity
import my.edu.tarc.communechat_v2.NotificationView
import my.edu.tarc.communechat_v2.Utility.myUtil
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.internal.MqttHelper
import my.edu.tarc.communechat_v2.model.Chat_Room
import my.edu.tarc.communechat_v2.model.Message
import my.edu.tarc.communechat_v2.model.User
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONException
import org.json.JSONObject

class BackgroundService : IntentService("MqttBackground") {
    val tag = "BackgroundService"
    val helper = MqttHelper()

    override fun onCreate() {
        super.onCreate()
        helper.connect(applicationContext)
    }

    override fun onHandleIntent(intent: Intent) {
        val roomID = intent.getIntArrayExtra(Chat_Room.COL_ROOM_ID)
        for (x in roomID) {
            val topic = MqttHeader.SEND_ROOM_MESSAGE + "/room" + x
            helper.connectSubscribe(applicationContext, topic)
        }
        helper.mqttClient.setCallback(roomCallback)
    }

    private val roomCallback = object : MqttCallback {
        override fun connectionLost(cause: Throwable) {

        }

        @Throws(Exception::class)
        override fun messageArrived(topic: String, message: MqttMessage) {
            Log.d(tag, "Receiving message: $message")
            val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val roomHelper = MqttHelper()
            val received_message = Message()
            roomHelper.decode(message.toString())
            if (roomHelper.receivedHeader == MqttHeader.SEND_ROOM_MESSAGE) {
                try {
                    val incomeMessage = JSONObject(roomHelper.receivedResult)

                    if (incomeMessage.getInt(Message.COL_SENDER_ID) == pref.getInt(User.COL_USER_ID, -1)) {
                        return
                    }

                    received_message.sender_id = incomeMessage.getInt(Message.COL_SENDER_ID)
                    received_message.message = incomeMessage.getString(Message.COL_MESSAGE)
                    received_message.setDate_created(incomeMessage.getString(Message.COL_DATE_CREATED))
                    received_message.message_type = incomeMessage.getString(Message.COL_MESSAGE_TYPE)
                    received_message.room_id = incomeMessage.getInt(Message.COL_ROOM_ID)
                    received_message.sender_name = incomeMessage.getString(Message.COL_SENDER_NAME)
                    if(!received_message.message_type.equals("Text")){
                        val media = Base64.decode(incomeMessage.getString(Message.COL_MEDIA), 0)
                        received_message.media = media
                    }else {
                        received_message.media = null
                    }
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
                    NotificationView.sendNotification(applicationContext,received_message);
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken) {

        }
    }
}