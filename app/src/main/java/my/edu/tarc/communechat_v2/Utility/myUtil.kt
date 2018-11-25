package my.edu.tarc.communechat_v2.Utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import my.edu.tarc.communechat_v2.R


object myUtil {
    const val VIBRATE_LONG = 500
    const val VIBRATE_SHORT = 100

    @Suppress("DEPRECATION")
    fun makeVibration(context: Context, duration: Int) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                vibrator.vibrate(duration.toLong())
            }
        }
    }

    fun makeSound(context: Context) {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context.applicationContext, notification)
            if (!ringtone.isPlaying) {
                ringtone.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun makeNotification(context: Context, title: String, text: String, intent: PendingIntent?, drawable: Drawable?) {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val builder = NotificationCompat.Builder(context, "Default")
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setWhen(System.currentTimeMillis())
                    .setContentText(text)
                    .setContentIntent(intent)
                    .setChannelId("TarChat")
                    .setSound(notification)

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "TarChat"
                val channel = NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT)
                mNotificationManager.createNotificationChannel(channel)
                builder.setChannelId(channelId)
            }

            mNotificationManager.notify(1, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun makeToast(context: Context, content: String, duration: Int) {
        Toast.makeText(context, content, duration).show()
    }

    fun makeToast(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }
}
