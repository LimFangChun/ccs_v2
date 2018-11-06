package my.edu.tarc.communechat_v2;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;

import my.edu.tarc.communechat_v2.chatEngine.ChatRoomActivity;
import my.edu.tarc.communechat_v2.chatEngine.MyDateTime;
import my.edu.tarc.communechat_v2.chatEngine.SelectContactActivity;
import my.edu.tarc.communechat_v2.chatEngine.database.Chat;
import my.edu.tarc.communechat_v2.internal.MqttHeader;

import static my.edu.tarc.communechat_v2.NotificationView.NOTIFICTION_DISMISS;
import static my.edu.tarc.communechat_v2.NotificationView.clearMessage;
import static my.edu.tarc.communechat_v2.NotificationView.getNotifID;
import static my.edu.tarc.communechat_v2.NotificationView.getReplyMessage;
import static my.edu.tarc.communechat_v2.chatEngine.ChatFragment.CURRENT_USER_ID;
import static my.edu.tarc.communechat_v2.chatEngine.ChatFragment.SELECTED_CHAT_ROOM_ID;
import static my.edu.tarc.communechat_v2.chatEngine.ChatFragment.SELECTED_CHAT_ROOM_UNIQUE_TOPIC;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static String REPLY_ACTION = "reply_action";
    private static String KEY_MESSAGE_ID = "key_message_id";
    private static String NOTIFICTION_TAPS = "notification_taps";



    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences prefs = context.getSharedPreferences("message", 0);

        if (NOTIFICTION_TAPS.equals(intent.getAction())) {
            int notificationID= intent.getIntExtra("notificationID", 0);
            NotificationView.setPendingNotificationsCount(0);
            nm.cancel(notificationID);
            clearMessage(String.valueOf(intent.getLongExtra(SELECTED_CHAT_ROOM_ID, 0)),context);
            Intent nextIntent = new Intent(context, ChatRoomActivity.class);
            nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            nextIntent.putExtra(SELECTED_CHAT_ROOM_ID, (intent.getLongExtra(SELECTED_CHAT_ROOM_ID, 0)));
            nextIntent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, (intent.getStringExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC)));
            context.startActivity(nextIntent);

        } else if(NOTIFICTION_DISMISS.equals(intent.getAction())){
            int notificationID= intent.getIntExtra("notificationID", 0);
            NotificationView.setPendingNotificationsCount(0);
            nm.cancel(notificationID);
            clearMessage(String.valueOf(intent.getLongExtra(SELECTED_CHAT_ROOM_ID, 0)),context);
            Log.v("testingNO", "Dismiss");
        }else if (REPLY_ACTION.equals(intent.getAction())) {
            MyDateTime myDateTime = new MyDateTime();
            int notificationID= intent.getIntExtra("notificationID", 0);

            long chatRoomId = intent.getLongExtra(SELECTED_CHAT_ROOM_ID, 0);
            String ChatRoomUniqueTopic=intent.getStringExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC);

            CharSequence message = getReplyMessage(intent);
            Chat chat = new Chat();
            chat.setMessage(message.toString());
            chat.setMessageType(Chat.TEXT_MESSAGE);
            chat.setRoomId(chatRoomId);
            chat.setSenderId(String.valueOf(CURRENT_USER_ID));
            //Log.v("2testing", intent.getStringExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC));
            chat.setChatRoomUniqueTopic(ChatRoomUniqueTopic);
            chat.setDate(myDateTime.getDateTime());
            chat.setComparingDateTime(String.valueOf(myDateTime.getCurrentTimeInMillisecond()));

            NotificationView.setPendingNotificationsCount(0);
            nm.cancel(notificationID);
            clearMessage(String.valueOf(chat.getRoomId()),context);
            if (isNetworkAvailable(context)) {

                MainActivity.mqttHelper.publish(
                        ChatRoomUniqueTopic,
                        MqttHeader.SEND_MESSAGE, chat
                );

                new ChatRoomActivity.UpdateAsyncTask(context, chat, chatRoomId).execute();

            }

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
