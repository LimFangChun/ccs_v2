package my.edu.tarc.communechat_v2.Utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public final class myUtil {
    private static final String TAG = "[myUtil]";
    public static final int VIBRATE_LONG = 500;
    public static final int VIBRATE_SHORT = 100;

    public static void makeVibration(Context context, int duration) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator == null) {
            Log.d(TAG, "makeVibration: vibrator is null, could not init vibrator");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(duration);
        }
    }

    public static void makeSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            if (!ringtone.isPlaying()) {
                ringtone.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeNotification(Context context, String title, String text, PendingIntent intent, Drawable drawable) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Default")
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(intent)
                    .setSound(notification);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.notify(0, builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeToast(Context context, String content, int duration) {
        Toast.makeText(context, content, duration).show();
    }
}
