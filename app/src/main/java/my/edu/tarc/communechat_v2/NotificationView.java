package my.edu.tarc.communechat_v2;

import android.app.Activity;
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

import java.util.List;


import co.intentservice.chatui.models.ChatMessage;
import my.edu.tarc.communechat_v2.model.Message;

import static android.support.constraint.Constraints.TAG;


public class NotificationView {

    private static String DEFAULT_CHANNEL_ID = "ccs_channel";
    private static String DEFAULT_CHANNEL_NAME = "CCS";
    private static String currentID = "";
    private static String KEY_REPLY = "notif_action_reply";
    private static int num;

    private static String REPLY_ACTION = "reply_action";
    private static String KEY_MESSAGE_ID = "key_message_id";
    private static int NOTIFICATION_ID;
    private Activity mActivity;


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
        //  if(notificationType.equals(PRIVATE_CHAT_ROOM)) {
        strRingtonePreference = pref.getString("ring_tone_pref", "content://settings/system/notification_sound");
        uri = Uri.parse(strRingtonePreference);
        isMute = pref.getBoolean("mute_key", false);
//        }else{
//            strRingtonePreference = pref.getString("group_ring_tone_pref", "content://settings/system/notification_sound");
//            uri = Uri.parse(strRingtonePreference);
//            isMute = pref.getBoolean("group_mute_key", false);
//        }

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

    public static void setNotifID(int id) {
        NOTIFICATION_ID = id;
    }

    public static int getNotifID() {
        return NOTIFICATION_ID;
    }

    public static void setNum(int n) {
        num = n;
    }

    public static int getNum() {
        return num;
    }

    public static void sendNotification(Context mContext, Message chat) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isOn = pref.getBoolean("notification_key", false);
        Uri uri = null;
        if (isOn) {

            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            uri = checkSettingPreferences(mContext, "");
            createNotificationChannel(mNotificationManager, uri);


            boolean showMessagePreview = pref.getBoolean("message_preview_key", true);
            String str = "New Message";

            if (showMessagePreview) {
                str = chat.getMessage();
            }
            Notification mNotification = new NotificationCompat.Builder(mContext, getChannelID())
                    // .setContentIntent(chatIntent)
                    .setContentTitle(chat.getSender_name())   //Set the title of Notification
                    .setContentText(str)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(uri)
                    .setAutoCancel(true)
                    .setShowWhen(true)
                    .setVibrate(new long[]{100,100,100,100,100})
                    // .addAction(replyAction)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .build();

            setNotifID(chat.getRoom_id());
            mNotificationManager.notify(getNotifID(), mNotification);
        }
    }
}





