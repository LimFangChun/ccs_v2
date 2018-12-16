package my.edu.tarc.communechat_v2;

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
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.communechat_v2.model.Message;

import static android.support.constraint.Constraints.TAG;


public class NotificationView {

    private static String DEFAULT_CHANNEL_ID = "ccs_channel";
    private static String DEFAULT_CHANNEL_NAME = "CCS";
    private static String currentID = "";
    private static String KEY_REPLY = "notif_action_reply";
    public static String ROOM_NAME;

    private static String REPLY_ACTION = "reply_action";
    private static String KEY_MESSAGE_ID = "key_message_id";
    public static String NOTIFICTION_DISMISS = "notification_dismiss";
    private static int NOTIFICATION_ID;
    public static String CHAT_ROOM_TYPE;
    public static String PRIVATE = "Private";
    public static String PUBLIC = "Public";


    private static int numMessage = 0;


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


    private static Uri checkSettingPreferences(Context context, String notificationType) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Uri uri = null;
        String strRingtonePreference;


        boolean isMute = false;
        if (notificationType.equals(PRIVATE)) {
            strRingtonePreference = pref.getString("ring_tone_pref", "content://settings/system/notification_sound");
            uri = Uri.parse(strRingtonePreference);
            isMute = pref.getBoolean("mute_key", false);
        } else {
            strRingtonePreference = pref.getString("group_ring_tone_pref", "content://settings/system/notification_sound");
            uri = Uri.parse(strRingtonePreference);
            isMute = pref.getBoolean("group_mute_key", false);
        }

        if (isMute) {
            uri = null;
        }
        Ringtone ringtone = RingtoneManager.getRingtone(
                context, Uri.parse(strRingtonePreference));
        Log.v(TAG, "path:" + getChannelID() + strRingtonePreference + "name:" + ringtone.getTitle(context));
        return uri;
    }


//    public static void sendNotification(Activity activity,Chat chat) {
//        setNum(getNum() + 1);
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
//        boolean isOn = pref.getBoolean("notification_key", false);
//        if (isOn) {
//            String strRingtonePreference = pref.getString("ring_tone_pref", "content://settings/system/notification_sound");
//            Uri uri = Uri.parse(strRingtonePreference);
//            boolean isMute = pref.getBoolean("mute_key", false);
//            if (isMute) {
//                uri = null;
//            }
//
//            NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
//            createNotificationChannel(mNotificationManager, uri);
//
//            Ringtone ringtone = RingtoneManager.getRingtone(
//                    activity, Uri.parse(strRingtonePreference));
//            Log.v(TAG, "path:" + getChannelID() + strRingtonePreference + "name:" + ringtone.getTitle(activity));
//
//
//            boolean showMessagePreview = pref.getBoolean("message_preview_key", true);
//            String str = "New Message";
//
//
//            if (showMessagePreview) {
//                str = chat.getMessage();
//            }
//
//            PendingIntent contentIntent = getPendingTntent(activity, chat);
//            Intent notificationIntent = new Intent(activity, ChatRoomActivity.class);
//            notificationIntent.setAction(REPLY_ACTION);
//            notificationIntent.putExtra(KEY_MESSAGE_ID, chat.getId());
//            notificationIntent.putExtra(SELECTED_CHAT_ROOM_ID, chat.getRoomId());
//            notificationIntent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, chat.getChatRoomUniqueTopic());
//            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            PendingIntent chatIntent = PendingIntent.getActivity(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            String replyLabel = "Reply";
//
//            RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
//                    .setLabel(replyLabel)
//                    .build();
//
//            NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
//                    R.drawable.ic_notif_action_reply, replyLabel, contentIntent)
//                    .addRemoteInput(remoteInput)
//                    .setAllowGeneratedReplies(true)
//                    .build();
//
//
//            //2.Build Notification with NotificationCompat.Builder
//            Notification mNotification = new NotificationCompat.Builder(activity, getChannelID())
//                    .setContentIntent(chatIntent)
//                    .setContentTitle(chat.getSenderId())   //Set the title of Notification
//                    .setContentText(str)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(str))
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setSound(uri)
//                    .setAutoCancel(true)
//                    .setShowWhen(true)
//                    .addAction(replyAction)
//                    .setVisibility(Notification.VISIBILITY_PUBLIC)
//                    .build();
//
//            setNotifID((int) chat.getId());
//            mNotificationManager.notify(getNotifID(), mNotification);
//        }
//    }
//        public static CharSequence getReplyMessage(Intent intent){
//            Bundle remoteInput= RemoteInput.getResultsFromIntent(intent);
//            if(remoteInput!=null){
//                return remoteInput.getCharSequence(KEY_REPLY);
//            }
//            return null;
//        }
//
//        private static PendingIntent getPendingTntent(Context activity,Chat chat){
//            Intent notificationIntent;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                notificationIntent = new Intent(activity, NotificationBroadcastReceiver.class);
//                notificationIntent.setAction(REPLY_ACTION);
//                notificationIntent.putExtra(KEY_MESSAGE_ID, chat.getId());
//                return  PendingIntent.getBroadcast(activity, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            }else{
//                notificationIntent = new Intent(activity, ChatRoomActivity.class);
//                notificationIntent.setAction(REPLY_ACTION);
//                notificationIntent.putExtra(KEY_MESSAGE_ID, chat.getId());
//                notificationIntent.putExtra(SELECTED_CHAT_ROOM_ID, chat.getRoomId());
//                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                return PendingIntent.getActivity(activity, 100, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//        }



    public static void sendNotification(Context mContext, Message chat) {
        String str = "";
        String notificationType = "";
        NOTIFICATION_ID = (int) chat.getRoom_id();
        CHAT_ROOM_TYPE = getChatRoomType();
        ROOM_NAME = getRoomName();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isOn = pref.getBoolean("notification_key", false);
        Uri uri = null;
        if (isOn) {
            //checkRoom(mContext,chat.getRoom_id());
            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Log.v("TESTING_NEW", CHAT_ROOM_TYPE + ROOM_NAME);
            List<String> oldMessage = loadArray(String.valueOf(chat.getRoom_id()), mContext);
            setPendingNotificationsCount(oldMessage.size() + 1);

            if (CHAT_ROOM_TYPE.equals(PUBLIC)) {
                notificationType = PUBLIC;
            } else
                notificationType = PRIVATE;

            String roomName = ROOM_NAME;
            String sender = chat.getSender_name();
            if (getPendingNotificationsCount() > 1) {
                if (CHAT_ROOM_TYPE.equals(PUBLIC)) {
                    roomName += " (" + getPendingNotificationsCount() + " message)";

                } else {
                    sender += " (" + getPendingNotificationsCount() + " message)";
                }
            }

            uri = checkSettingPreferences(mContext, notificationType);
            createNotificationChannel(mNotificationManager, uri);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(chat.getSender_name()).setConversationTitle(roomName);

            String incomingMessage = chat.getMessage();

            Log.v("testingLOADSIZE", String.valueOf(oldMessage.size()));

            if (oldMessage.size() > 0) {
                oldMessage.add(new String(incomingMessage));
                boolean store = saveArray(oldMessage, String.valueOf(chat.getRoom_id()), mContext);

                for (int i = 0; i < oldMessage.size(); i++) {
                    if (CHAT_ROOM_TYPE.equals(PUBLIC)) {
                        messagingStyle.addMessage(oldMessage.get(i), 0, chat.getSender_name());
                    } else {
                        inboxStyle.addLine(oldMessage.get(i));
                    }

                }
            } else {
                List<String> message = new ArrayList<String>();
                message.add(new String(incomingMessage));
                if (CHAT_ROOM_TYPE.equals(PUBLIC)) {
                    messagingStyle.addMessage(incomingMessage, 0, chat.getSender_name());
                } else {
                    inboxStyle.addLine(incomingMessage);
                    str = incomingMessage;
                }
                boolean store = saveArray(message, String.valueOf(chat.getRoom_id()), mContext);
            }

            boolean showMessagePreview = pref.getBoolean("message_preview_key", true);
            if (oldMessage.size() == 0) {
                if (showMessagePreview) {
                    str = chat.getMessage();
                } else {
                    str = "You have a new message";
                }
            } else {
                if (!showMessagePreview) {
                    str = "You have " + oldMessage.size() + " new message";
                }
            }


            Intent dismissIntent = new Intent(mContext, NotificationBroadcastReceiver.class);
            dismissIntent.putExtra("notificationID", NOTIFICATION_ID);
            dismissIntent.setAction(NOTIFICTION_DISMISS);
            dismissIntent.putExtra("SELECTED_CHAT_ROOM_ID", chat.getRoom_id());
            PendingIntent dismissNotification = PendingIntent.getBroadcast(mContext, NOTIFICATION_ID, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            if (CHAT_ROOM_TYPE.equals(PRIVATE)) {
                setNotifID(NOTIFICATION_ID);
                NotificationCompat.Builder mNotification = new NotificationCompat.Builder(mContext, getChannelID());
                //.setContentIntent(chatIntent)
                mNotification.setContentTitle(sender);  //Set the title of Notification
                mNotification.setContentText(str);
                mNotification.setSmallIcon(R.mipmap.ic_launcher);
                mNotification.setSound(uri);
                mNotification.setNumber(numMessage);
                mNotification.setAutoCancel(true);
                mNotification.setShowWhen(true);
                //.setPriority(priority)
                //.addAction(replyAction)
                mNotification.setVibrate(new long[]{100, 100, 100, 100, 100});
                mNotification.setDeleteIntent(dismissNotification);
                mNotification.setVisibility(Notification.VISIBILITY_PUBLIC);
                if (showMessagePreview) {
                    mNotification.setStyle(inboxStyle);
                } else {
                    mNotification.setContentText(str);
                }
                mNotificationManager.notify(getNotifID(), mNotification.build());
            } else {
                setNotifID(NOTIFICATION_ID);
                NotificationCompat.Builder mNotification = new NotificationCompat.Builder(mContext, getChannelID())
                        .setStyle(messagingStyle)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //.setContentIntent(chatIntent)
                        .setSound(uri)
                        .setAutoCancel(true)
                        .setNumber(numMessage)
                        .setShowWhen(true)
                        .setVibrate(new long[]{100, 100, 100, 100, 100})
                        //.setPriority(priority)
                        //.addAction(replyAction)
                        .setDeleteIntent(dismissNotification)
                        .setVisibility(Notification.VISIBILITY_PUBLIC);

                mNotificationManager.notify(getNotifID(), mNotification.build());
            }
        }
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


        for (int i = 0; i < size; i++) {
            message.add(new String(prefs.getString(arrayName + "_" + i, null)));
        }

        return message;
    }

    public static void clearMessage(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("message", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", 0);
        editor.remove(arrayName);
        editor.apply();
        editor.commit();


    }

    public static void setNotifID(int id) {
        NOTIFICATION_ID = id;
    }

    public static int getNotifID() {
        return NOTIFICATION_ID;
    }

    public static int getPendingNotificationsCount() {
        return numMessage;
    }

    public static void setPendingNotificationsCount(int pendingNotifications) {
        numMessage = pendingNotifications;
    }

    public static String getRoomName() {
        return ROOM_NAME;
    }

    public static void setRoomName(String roomName) {
        ROOM_NAME = roomName;
    }

    public static String getChatRoomType() {
        return CHAT_ROOM_TYPE;
    }

    public static void setChatRoomType(String chatRoomType) {
        CHAT_ROOM_TYPE = chatRoomType;
    }

}





