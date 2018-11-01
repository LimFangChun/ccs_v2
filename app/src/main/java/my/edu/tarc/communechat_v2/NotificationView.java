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
    private static List<ChatRoom> mChatRoomList;
    private static String REPLY_ACTION = "reply_action";
    private static String KEY_MESSAGE_ID = "key_message_id";
    private static int NOTIFICATION_ID;
    private static String NOTIFICTION_TAPS = "notification_taps";
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

                NotificationChannel notificationChannel = new NotificationChannel(currentID, DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setShowBadge(true);
                notificationChannel.setSound(uri, attrs.build());
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(notificationChannel);
            } else {
                currentID = DEFAULT_CHANNEL_ID;
                NotificationChannel notificationChannel = new NotificationChannel(currentID, DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
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


    private static Uri checkSettingPreferences(Activity activity) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);

        String strRingtonePreference = pref.getString("ring_tone_pref", "content://settings/system/notification_sound");
        Uri uri = Uri.parse(strRingtonePreference);
        boolean isMute = pref.getBoolean("mute_key", false);
        if (isMute) {
            uri = null;
        }
        Ringtone ringtone = RingtoneManager.getRingtone(
                activity, Uri.parse(strRingtonePreference));
        Log.v(TAG, "path:" + getChannelID() + strRingtonePreference + "name:" + ringtone.getTitle(activity));


        return uri;
    }

    public static void sendNotification(Activity activity, Chat chat, ChatRoom chatRoom) {


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean isOn = pref.getBoolean("notification_key", false);
        Uri uri = null;
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (isOn) {
            uri = checkSettingPreferences(activity);
            createNotificationChannel(mNotificationManager, uri);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            setPendingNotificationsCount(getPendingNotificationsCount() + 1);
            String incomingMessage=chat.getMessage();
            String[] message = new String[getPendingNotificationsCount()];
            String[] oldMessage=loadArray(String.valueOf(chat.getRoomId()),activity);
//            if(oldMessage.length!=0){
//                Log.v("testingLINE", incomingMessage);
//                List<String> oldMessages = Arrays.asList(oldMessage);
//                oldMessages.add(incomingMessage);
//                message=oldMessages.toArray(new String[oldMessage.length]);
//                boolean store = saveArray(message, String.valueOf(chat.getRoomId()), activity);
//                for (int i = oldMessages.size()-1; i <=oldMessages.size() ; i++) {
//                    Log.v("testingLINE", "wwwwwwwwwww");
//                     inboxStyle.addLine(oldMessages.get(i));
//                     Log.v("testingLINE", oldMessages.get(i));
//                }
//            }else{
//                Log.v("testingLINE", "qqqqqqqq");
//                message[getPendingNotificationsCount() - 1] = incomingMessage;
//                boolean store = saveArray(message, String.valueOf(chat.getRoomId()), activity);
//            }

            Log.v("testingLINE", String.valueOf(getPendingNotificationsCount()));
            //String[] oldMessage=loadArray(String.valueOf(chat.getRoomId()),activity);
//            if(oldMessage.length!=0){
//                List<String> messages = Arrays.asList(oldMessage);
//                Log.i(TAG,"showSmallNotification "+ messages);
//                Log.v("testingLINE", String.valueOf(messages.size()));
//                for (int i = messages.size()-1; i <=messages.size() ; i++) {
//                    Log.v("testingLINE", "wwwwwwwwwww");
//                    // inboxStyle.addLine(messages.get(i));
//                   // Log.v("testingLINE", messages.get(i));
//                }
//            }


            boolean showMessagePreview = pref.getBoolean("message_preview_key", true);
            String str = "You have a new message";
            String roomName = "Group";
            if (chatRoom.getChatRoomType().equals(GROUP_CHAT_ROOM)) {
                roomName = chatRoom.getName();
            }
            String sender = chat.getSenderId();
            if (getPendingNotificationsCount() > 1) {
                if (chatRoom.getChatRoomType().equals(GROUP_CHAT_ROOM)) {
                    roomName = chatRoom.getName();
                    roomName += " (" + getPendingNotificationsCount() + " message)";

                } else {
                    sender += " (" + getPendingNotificationsCount() + " message)";
                }
            }


            if (showMessagePreview) {
                str = chat.getMessage();
            }

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

            Log.v("1testing", String.valueOf(chat.getRoomId()));
            Log.v("1testing", String.valueOf(chat.getChatRoomUniqueTopic()));
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

            if (chatRoom.getChatRoomType().equals(PRIVATE_CHAT_ROOM)) {
                NOTIFICATION_ID = (int) chat.getRoomId();
                Log.v("testing", String.valueOf(NOTIFICATION_ID));
                Notification mNotification = new NotificationCompat.Builder(activity, getChannelID())
                        .setContentIntent(chatIntent)
                        .setContentTitle(sender)   //Set the title of Notification
                        .setContentText(str)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(uri)
                        .setNumber(numMessage)
                        .setAutoCancel(true)
                        .setShowWhen(true)
                        .addAction(replyAction)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        //.setStyle(inboxStyle)
                        .build();
                setNotifID(NOTIFICATION_ID);
                Log.v("testingP", String.valueOf(NOTIFICATION_ID));
                mNotificationManager.notify(getNotifID(), mNotification);

            } else {
                NOTIFICATION_ID = (int) chat.getRoomId();
                NotificationCompat.Builder mNotification = new NotificationCompat.Builder(activity, getChannelID())
                        .setStyle(new NotificationCompat.MessagingStyle(sender).setConversationTitle(roomName)
                                .addMessage(str, 0, sender))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(chatIntent)
                        .setSound(uri)
                        .setAutoCancel(true)
                        .setNumber(numMessage)
                        .setShowWhen(true)
                        .addAction(replyAction)
                        .setVisibility(Notification.VISIBILITY_PUBLIC);
                setNotifID(NOTIFICATION_ID);
                Log.v("testingG", String.valueOf(NOTIFICATION_ID));
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

    public static boolean saveArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("message", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    public static String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("message", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }

    public static void clearMessage(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("message", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        prefs.edit().clear();
    }

    public static int getPendingNotificationsCount() {
        return numMessage;
    }

    public static void setPendingNotificationsCount(int pendingNotifications) {
        numMessage = pendingNotifications;
    }

}


