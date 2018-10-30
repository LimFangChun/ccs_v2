package my.edu.tarc.communechat_v2;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import my.edu.tarc.communechat_v2.chatEngine.MyDateTime;
import my.edu.tarc.communechat_v2.chatEngine.database.Chat;

import static my.edu.tarc.communechat_v2.NotificationView.getNotifID;
import static my.edu.tarc.communechat_v2.NotificationView.getReplyMessage;
import static my.edu.tarc.communechat_v2.chatEngine.ChatFragment.SELECTED_CHAT_ROOM_ID;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static String REPLY_ACTION="reply_action";
    private static String KEY_MESSAGE_ID="key_message_id";
    private EditText mMessageBoxEditText;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (REPLY_ACTION.equals(intent.getAction())) {
            CharSequence message = getReplyMessage(intent);

            Chat chat = new Chat();
            chat.setDate(new MyDateTime().getDateTime());
            chat.setMessage(message.toString());
            chat.setMessageType(Chat.TEXT_MESSAGE);
            chat.setRoomId(intent.getIntExtra(SELECTED_CHAT_ROOM_ID,0));
            chat.setSenderId(String.valueOf(chat.getSenderId()));
            chat.setChatRoomUniqueTopic(chat.getChatRoomUniqueTopic());

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(getNotifID());
        }
    }
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
