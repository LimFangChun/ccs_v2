package my.edu.tarc.communechat_v2.Utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import my.edu.tarc.communechat_v2.R
import java.io.File
import java.util.*


object MyUtil {
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

    fun makeNotification(context: Context, title: String, text: String, intent: PendingIntent?) {
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

    fun getLocalImagePath(context: Context): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        //val rootPath = Environment.getExternalStorageDirectory().toString() + "/Tarc"
        //val rootPath = pref.getString(context.getString(R.string.pref_storagePath), context.filesDir.toString() + "/Tarc")
        val rootPath = context.filesDir.toString() + "/Tarc"
        val imageDir = File(rootPath)
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        return imageDir.absolutePath
    }

    fun openPlayStore(context: Context, targetAppName: String) {
        val url = try {
            //Check whether Google Play store is installed or not:
            context.packageManager.getPackageInfo("com.android.vending", 0)

            "market://details?id=$targetAppName"
        } catch (e: Exception) {
            "https://play.google.com/store/apps/details?id=$targetAppName"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun isToday(time: Calendar): Boolean {
        val calendar = Calendar.getInstance()
        return time.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun isSameHour(time: Calendar): Boolean {
        val calendar = Calendar.getInstance()
        return time.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY)
    }
}
