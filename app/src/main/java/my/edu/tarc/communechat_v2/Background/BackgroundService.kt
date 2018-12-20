package my.edu.tarc.communechat_v2.Background

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import my.edu.tarc.communechat_v2.MainActivity
import my.edu.tarc.communechat_v2.NotificationView
import my.edu.tarc.communechat_v2.NotificationView.setChatRoomType
import my.edu.tarc.communechat_v2.NotificationView.setRoomName
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.internal.MqttHelper
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper
import my.edu.tarc.communechat_v2.model.AdvancedEncryptionStandard
import my.edu.tarc.communechat_v2.model.Chat_Room
import my.edu.tarc.communechat_v2.model.Message
import my.edu.tarc.communechat_v2.model.User
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class BackgroundService : IntentService("MqttBackground") {
    val tag = "BackgroundService"
    val helper = MqttHelper()
    val mqttHelper = MqttHelper()
    val received_message = Message()

    override fun onCreate() {
        super.onCreate()
        helper.connect(applicationContext)

        val temp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val roomID = stringToIntArray(temp.getString(Chat_Room.COL_ROOM_ID, ""))
        for (x in roomID) {
            val topic = MqttHeader.SEND_ROOM_MESSAGE + "/room" + x
            helper.connectSubscribe(applicationContext, topic)
        }
        helper.mqttClient.setCallback(roomCallback)
        Log.d("BackgroundService", "onCreate called")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return IntentService.START_STICKY
        //todo test
    }

    override fun onHandleIntent(intent: Intent) {
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BackgroundService", "onDestroy called, service shutdown")
    }

    private val roomCallback = object : MqttCallback {
        override fun connectionLost(cause: Throwable) {

        }

        @Throws(Exception::class)
        override fun messageArrived(topic: String, message: MqttMessage) {
            Log.d(tag, "Receiving message: $message")
            val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val roomHelper = MqttHelper()
            //val received_message = Message()
            roomHelper.decode(message.toString())
            if (roomHelper.receivedHeader == MqttHeader.SEND_ROOM_MESSAGE || roomHelper.receivedHeader == MqttHeader.SEND_ROOM_IMAGE) {
                try {
                    val incomeMessage = JSONObject(roomHelper.receivedResult)

                    if (incomeMessage.getInt(Message.COL_SENDER_ID) == pref.getInt(User.COL_USER_ID, -1)) {
                        return
                    }
                    val chat_room = Chat_Room()
                    chat_room.room_id = incomeMessage.getInt(Message.COL_ROOM_ID)
                    val secretKey = pref!!.getString(RoomSecretHelper.getRoomPrefKey(chat_room.room_id), null)


                    val topic = "checkRoomType/" + chat_room.room_id
                    val header = MqttHeader.CHECK_ROOM_TYPE
                    mqttHelper.connectPublishSubscribe(applicationContext, topic, header, chat_room)
                    mqttHelper.mqttClient.setCallback(getRoomTypeCallback)
                    received_message.sender_id = incomeMessage.getInt(Message.COL_SENDER_ID)
                    received_message.room_id = incomeMessage.getInt(Message.COL_ROOM_ID)
                    received_message.sender_name = incomeMessage.getString(Message.COL_SENDER_NAME)
                    received_message.date_created = Calendar.getInstance()
                    received_message.message_type = incomeMessage.getString(Message.COL_MESSAGE_TYPE)
                    if (received_message.message_type != "Text") {
                        received_message.message = "[Image]"
                    }else {
                        if (secretKey != null)
                            received_message.message = AdvancedEncryptionStandard(secretKey).decrypt(incomeMessage.getString(Message.COL_MESSAGE))
                        else {
                            received_message.message = incomeMessage.getString(Message.COL_MESSAGE)
                        }
                    }
                    Log.v("TESTING", received_message.message_type + "wwwww")
                    Log.v("TESTING", received_message.sender_name + "wwwww")
                    Log.v("TESTING", received_message.date_created.toString())
                    Log.v("TESTING", received_message.sender_id.toString())


                    val intent = Intent(applicationContext, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, 0)


//                    MyUtil.makeNotification(
//                            context = applicationContext,
//                            title = incomeMessage.getString(Message.COL_SENDER_NAME),
//                            text = incomeMessage.getString(Message.COL_MESSAGE),
//                            intent = pendingIntent)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken) {

        }
    }
    private val getRoomTypeCallback = object : MqttCallback {
        override fun connectionLost(cause: Throwable) {

        }

        @Throws(Exception::class)
        override fun messageArrived(topic: String, message: MqttMessage) {
            val helper = MqttHelper()
            helper.decode(message.toString())
            if (helper.receivedHeader == MqttHeader.CHECK_ROOM_TYPE_REPLY) {
                val receivedResult = helper.receivedResult
                try {
                    val jsonResult = JSONArray(receivedResult)


                    val temp = jsonResult.getJSONObject(0)
                    setRoomName(temp.getString("room_name"))
                    setChatRoomType(temp.getString("room_type"))


                    NotificationView.sendNotification(applicationContext, received_message);


                } catch (e: Exception) {
                    e.printStackTrace()
                }

                helper.unsubscribe(topic)
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken) {

        }
    }

    private fun stringToIntArray(string: String): IntArray {
        var temp = string.replace("[", "")
        temp = temp.replace("]", "")

        val temp2 = temp.split(",")
        var result = IntArray(temp2.size)
        for (x in temp2) {
            result += (x.trim().toInt())
        }

        return result
    }
}
