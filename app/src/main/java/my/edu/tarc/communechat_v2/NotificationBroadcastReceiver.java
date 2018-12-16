package my.edu.tarc.communechat_v2;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static my.edu.tarc.communechat_v2.NotificationView.NOTIFICTION_DISMISS;
import static my.edu.tarc.communechat_v2.NotificationView.clearMessage;


public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static String REPLY_ACTION = "reply_action";
    private static String KEY_MESSAGE_ID = "key_message_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences prefs = context.getSharedPreferences("message", 0);
        if (NOTIFICTION_DISMISS.equals(intent.getAction())) {
            int notificationID = intent.getIntExtra("notificationID", 0);
            NotificationView.setPendingNotificationsCount(0);
            nm.cancel(notificationID);
            clearMessage(String.valueOf(intent.getIntExtra("SELECTED_CHAT_ROOM_ID", 0)), context);
            Log.v("testingNO", "Dismiss");
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
