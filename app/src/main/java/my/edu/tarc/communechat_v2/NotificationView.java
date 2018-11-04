package my.edu.tarc.communechat_v2;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.edu.tarc.communechat_v2.chatEngine.ChatFragment;
import my.edu.tarc.communechat_v2.chatEngine.ChatRoomActivity;
import my.edu.tarc.communechat_v2.chatEngine.database.Chat;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;
import static android.support.constraint.Constraints.TAG;
import static my.edu.tarc.communechat_v2.chatEngine.ChatFragment.SELECTED_CHAT_ROOM_ID;
import static my.edu.tarc.communechat_v2.chatEngine.ChatFragment.SELECTED_CHAT_ROOM_UNIQUE_TOPIC;
import static my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom.GROUP_CHAT_ROOM;
import static my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom.PRIVATE_CHAT_ROOM;


public class NotificationView extends Application {

    private static String DEFAULT_CHANNEL_ID = "ccs_channel";
    private static String DEFAULT_CHANNEL_NAME = "CCS";
    private static String currentID = "";
    private static String KEY_REPLY = "notif_action_reply";
    private static String REPLY_ACTION = "reply_action";
    private static String KEY_MESSAGE_ID = "key_message_id";
    private static int NOTIFICATION_ID;
    private static String NOTIFICTION_TAPS = "notification_taps";
    public static String NOTIFICTION_DISMISS = "notification_dismiss";
    private static int numMessage = 0;
    //private static String[] message = new String[50];

    public void onCreate() {
        super.onCreate();
    }

    /*
     * Create NotificationChannel as required from Android 8.0 (Oreo)
     * */
    public static void createNotificationChannel(NotificationManager notificationManager, Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Create channel only if it is not already created
            AudioAttributes.Builder attrs = new AudioAttributes.Builder();
            attrs.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            attrs.setUsage(AudioAttributes.USAGE_NOTIFICATION);

            NotificationChannel mChannel = notificationManager.getNotificationChannel(DEFAULT_CHANNEL_ID);
            if (mChannel != null && !mChannel.getSound().equals(uri)) {
                String nm = null;
                if (uri != null) {
                    nm = uri.getPath();
                }
                currentID = DEFAULT_CHANNEL_ID + "" + nm;
                Log.v(TAG, "path:" + mChannel.getSound() + currentID);

                NotificationChannel notificationChannel = new NotificationChannel(currentID, DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);
                notificationChannel.setShowBadge(true);
                notificationChannel.setSound(uri, attrs.build());
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(notificationChannel);
            } else {
                currentID = DEFAULT_CHANNEL_ID;
                NotificationChannel notificationChannel = new NotificationChannel(currentID, DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);
                notificationChannel.setShowBadge(true);
                notificationChannel.setSound(uri, attrs.build());
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Log.v(TAG, "path:" + uri);

        }
    }

    public static String getChannelID() {
        return currentID;
    }


    private static Uri checkSettingPreferences(Activity activity,String notificationType) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        Uri uri=null;
        String strRingtonePreference;


        boolean isMute=false;
        if(notificationType.equals(PRIVATE_CHAT_ROOM)) {
            strRingtonePreference = pref.getString("ring_tone_pref", "content://settings/system/notification_sound");
            uri = Uri.parse(strRingtonePreference);
            isMute = pref.getBoolean("mute_key", false);
        }else{
            strRingtonePreference = pref.getString("group_ring_tone_pref", "content://settings/system/notification_sound");
            uri = Uri.parse(strRingtonePreference);
            isMute = pref.getBoolean("group_mute_key", false);
        }

        if (isMute) {
            uri = null;
        }
        Ringtone ringtone = RingtoneManager.getRingtone(
                activity, Uri.parse(strRingtonePreference));
        Log.v(TAG, "path:" + getChannelID() + strRingtonePreference + "name:" + ringtone.getTitle(activity));
        return uri;
    }

    public static void sendNotification(Activity activity, Chat chat, ChatRoom chatRoom) {
        String str = "";
        String notificationType="";

        List<String> oldMessage=loadArray(String.valueOf(chat.getRoomId()),activity);
        setPendingNotificationsCount(oldMessage.size()+1);

        String roomName = "Group";
        if (chatRoom.getChatRoomType().equals(GROUP_CHAT_ROOM)) {
            roomName = chatRoom.getName();
            notificationType=GROUP_CHAT_ROOM;
        }else
            notificationType=PRIVATE_CHAT_ROOM;

        String sender = chat.getSenderId();
        if (getPendingNotificationsCount() > 1) {
            if (chatRoom.getChatRoomType().equals(GROUP_CHAT_ROOM)) {
                roomName = chatRoom.getName();
                roomName += " (" + getPendingNotificationsCount() + " message)";

            } else {
                sender += " (" + getPendingNotificationsCount() + " message)";
            }
        }



        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean isOn = pref.getBoolean("notification_key", false);
        Uri uri = null;

        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (isOn) {

            uri = checkSettingPreferences(activity,notificationType);
            createNotificationChannel(mNotificationManager, uri);



            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            NotificationCompat.MessagingStyle messageingStyle =new NotificationCompat.MessagingStyle(sender).setConversationTitle(roomName);

            String incomingMessage=chat.getMessage();

            Log.v("testingLOADSIZE", String.valueOf(oldMessage.size()));
            if(oldMessage.size()>0){
                oldMessage.add(new String(incomingMessage));
                boolean store = saveArray(oldMessage, String.valueOf(chat.getRoomId()), activity);

                for (int i = 0; i <oldMessage.size() ; i++) {
                    if(chatRoom.getChatRoomType().equals(GROUP_CHAT_ROOM)){
                        messageingStyle.addMessage(oldMessage.get(i),0,sender);
                    }else {
                        inboxStyle.addLine(oldMessage.get(i));
                    }

                }
            }else{
                List<String> message = new ArrayList<String>();
                message.add(new String (incomingMessage));
                if(chatRoom.getChatRoomType().equals(GROUP_CHAT_ROOM)){
                    messageingStyle.addMessage(incomingMessage,0,sender);
                }else {
                    inboxStyle.addLine(incomingMessage);
                    str = incomingMessage;
                }
                boolean store = saveArray(message, String.valueOf(chat.getRoomId()), activity);
            }




            boolean showMessagePreview = pref.getBoolean("message_preview_key", true);
            if (!showMessagePreview) {
                str = "You have a new message";
            }else{
                str=incomingMessage;
            }

            Intent dismissIntent = new Intent(activity, NotificationBroadcastReceiver.class);
            dismissIntent.setAction(NOTIFICTION_DISMISS);
            PendingIntent dismissNotification = PendingIntent.getBroadcast(activity, 100, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent contentIntent = getPendingIntent(activity, chat);
            Intent notificationIntent = new Intent(activity, NotificationBroadcastReceiver.class);
            if (chatRoom.getChatRoomType().equals(PRIVATE_CHAT_ROOM)) {
                notificationIntent.putExtra("ROOM_TYPE",PRIVATE_CHAT_ROOM);
            } else {
                notificationIntent.putExtra("ROOM_TYPE",GROUP_CHAT_ROOM);

            }
            notificationIntent.setAction(NOTIFICTION_TAPS);
            notificationIntent.putExtra(KEY_MESSAGE_ID, chat.getId());

            notificationIntent.putExtra("SENDER_ID", chat.getSenderId());
            notificationIntent.putExtra(SELECTED_CHAT_ROOM_ID, chat.getRoomId());

            // Only private message is where the topic is equal to current user id
            if (chat.getChatRoomUniqueTopic().equals(String.valueOf(ChatFragment.CURRENT_USER_ID))) {
                notificationIntent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, chat.getSenderId());
            } else {
                notificationIntent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, chat.getChatRoomUniqueTopic());
            }


            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent chatIntent = PendingIntent.getBroadcast(activity, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            String replyLabel = "Reply";

            RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                    .setLabel(replyLabel)
                    .build();

            PendingIntent DirectReply;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                 DirectReply = contentIntent;
            }else{
                DirectReply = chatIntent;
                            }
            NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                    R.drawable.ic_notif_action_reply, replyLabel, DirectReply)
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(true)
                    .build();

            int priority=4;
            boolean popUp;


            if (chatRoom.getChatRoomType().equals(PRIVATE_CHAT_ROOM)) {
                NOTIFICATION_ID = (int) chat.getRoomId();
                popUp = pref.getBoolean("pop_up", true);
                if(popUp){
                    priority=NotificationCompat.PRIORITY_HIGH;
                }else{
                    priority=NotificationCompat.PRIORITY_LOW;
                }
                Notification mNotification = new NotificationCompat.Builder(activity, getChannelID())
                        .setContentIntent(chatIntent)
                        .setContentTitle(sender)   //Set the title of Notification
                        .setContentText(str)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(uri)
                        .setNumber(numMessage)
                        .setAutoCancel(true)
                        .setShowWhen(true)
                        .setPriority(priority)
                        .addAction(replyAction)
                        .setDeleteIntent(dismissNotification)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setStyle(inboxStyle)
                        .build();
                setNotifID(NOTIFICATION_ID);

                mNotificationManager.notify(getNotifID(), mNotification);

            } else {
                NOTIFICATION_ID = (int) chat.getRoomId();
                popUp = pref.getBoolean("group_pop_up", true);
                if(popUp){
                    priority=NotificationCompat.PRIORITY_HIGH;
                }else{
                    priority=NotificationCompat.PRIORITY_LOW;
                }
                NotificationCompat.Builder mNotification = new NotificationCompat.Builder(activity, getChannelID())
                        .setStyle(messageingStyle)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(chatIntent)
                        .setSound(uri)
                        .setAutoCancel(true)
                        .setNumber(numMessage)
                        .setShowWhen(true)
                        .setPriority(priority)
                        .addAction(replyAction)
                        .setDeleteIntent(dismissNotification)
                        .setVisibility(Notification.VISIBILITY_PUBLIC);
                setNotifID(NOTIFICATION_ID);


                mNotificationManager.notify(getNotifID(), mNotification.build());

            }
        }


    }

    public static CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }

    private static PendingIntent getPendingIntent(Context activity, Chat chat) {
        Intent notificationIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationIntent = new Intent(activity, NotificationBroadcastReceiver.class);
            notificationIntent.setAction(REPLY_ACTION);
            notificationIntent.putExtra(KEY_MESSAGE_ID, chat.getId());
            notificationIntent.putExtra(SELECTED_CHAT_ROOM_ID, chat.getRoomId());
            if (chat.getChatRoomUniqueTopic().equals(String.valueOf(ChatFragment.CURRENT_USER_ID))) {
                notificationIntent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, chat.getSenderId());
            } else {
                notificationIntent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, chat.getChatRoomUniqueTopic());
            }
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return PendingIntent.getBroadcast(activity, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {

            notificationIntent = new Intent(activity, ChatRoomActivity.class);
            notificationIntent.setAction(REPLY_ACTION);
            notificationIntent.putExtra(KEY_MESSAGE_ID, chat.getId());
            notificationIntent.putExtra(SELECTED_CHAT_ROOM_ID, chat.getRoomId());
            if (chat.getChatRoomUniqueTopic().equals(String.valueOf(ChatFragment.CURRENT_USER_ID))) {
                notificationIntent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, chat.getSenderId());
            } else {
                notificationIntent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, chat.getChatRoomUniqueTopic());
            }
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return PendingIntent.getActivity(activity, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public static void setNotifID(int id) {
        NOTIFICATION_ID = id;
    }

    public static int getNotifID() {
        return NOTIFICATION_ID;
    }

    public static boolean saveArray(List<String> message, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("message", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", message.size());
        for (int i = 0; i < message.size(); i++)
            editor.putString(arrayName + "_" + i, message.get(i));
        return editor.commit();
    }

    public static List<String> loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("message", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        List<String> message = new ArrayList<String>();

            Log.v("Loopsize", String.valueOf(size));
            for (int i = 0; i < size; i++) {
                message.add(new String( prefs.getString(arrayName + "_" + i, null)));
            }

        return message;
    }

    public static void clearMessage(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("message", 0);
        SharedPreferences.Editor editor=prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static int getPendingNotificationsCount() {
        return numMessage;
    }

    public static void setPendingNotificationsCount(int pendingNotifications) {
        numMessage = pendingNotifications;
    }

}


