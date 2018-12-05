package my.edu.tarc.communechat_v2.Background

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import my.edu.tarc.communechat_v2.MainActivity
import my.edu.tarc.communechat_v2.Utility.MyUtil
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
            roomHelper.decode(message.toString())
            if (roomHelper.receivedHeader == MqttHeader.SEND_ROOM_MESSAGE) {
                try {
                    val incomeMessage = JSONObject(roomHelper.receivedResult)

                    if (incomeMessage.getInt(Message.COL_SENDER_ID) == pref.getInt(User.COL_USER_ID, -1)) {
                        return
                    }

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
                    MyUtil.makeNotification(
                            context = applicationContext,
                            title = incomeMessage.getString(Message.COL_SENDER_NAME),
                            text = incomeMessage.getString(Message.COL_MESSAGE),
                            intent = pendingIntent)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
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