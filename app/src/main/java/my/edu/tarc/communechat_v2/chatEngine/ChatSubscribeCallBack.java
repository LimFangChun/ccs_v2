package my.edu.tarc.communechat_v2.chatEngine;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.chatEngine.database.ApplicationDatabase;
import my.edu.tarc.communechat_v2.chatEngine.database.Chat;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;
import my.edu.tarc.communechat_v2.chatEngine.database.ServerDatabase;
import my.edu.tarc.communechat_v2.internal.MqttHeader;

import static android.content.Context.MODE_PRIVATE;

public class ChatSubscribeCallBack implements MqttCallback {

    private static final String TAG = "ChatSubscribeCallBack";
    private Activity mActivity;
    private static String checker;

    public ChatSubscribeCallBack(Activity activity) {
        checker = "Empty";
        mActivity = activity;
    }


    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.i(TAG, "Topic: " + topic);
        Log.i(TAG, "Message: " + message);


        MainActivity.mqttHelper.decode(message.toString());
        String[] processMessage = MainActivity.mqttHelper.getReceivedResult().split(",");

        //Use to prevent message from receiving twice
        if (!checker.equals(MainActivity.mqttHelper.getReceivedResult())) {
            checker = MainActivity.mqttHelper.getReceivedResult();
            switch (MainActivity.mqttHelper.getReceivedHeader()) {
                case MqttHeader.SEND_MESSAGE:
                    new UpdateAsyncTask(mActivity, processMessage).execute();
                    break;
                case MqttHeader.ADD_GROUP_CHAT_ROOM:
                    new InsertGroupChatRoomAsyncTask(mActivity, processMessage).execute();
                    break;
            }
        }


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    private static class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Activity> mWeakReference;
        private String[] mProcessMessage;

        private UpdateAsyncTask(Activity activity, String[] processMessage) {
            mWeakReference = new WeakReference<>(activity);
            mProcessMessage = processMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(mWeakReference.get());

            //ChatRoom chatRoom = applicationDatabase.chatRoomDao().searchExistingChatRoomString(mProcessMessage[4]);

            Chat chat = new Chat();
            chat.setMessage(mProcessMessage[0]);
            chat.setSenderId(mProcessMessage[1]);
            chat.setDate(mProcessMessage[2]);
            chat.setMessageType(mProcessMessage[3]);
            chat.setChatRoomUniqueTopic(mProcessMessage[4]);

            //Prevent message from being update twice
            if (!applicationDatabase.chatDao().checkRepetition(chat.getChatRoomUniqueTopic(), chat.getMessage(), chat.getDate(), chat.getSenderId())) {
                ChatRoom chatRoomGroup = applicationDatabase.chatRoomDao().getGroupChatRoom(ChatRoom.GROUP_CHAT_ROOM, mProcessMessage[4]);

                if (chatRoomGroup == null) {
                    ChatRoom chatRoom = applicationDatabase.chatRoomDao().searchExistingChatRoom(mProcessMessage[1], ChatRoom.PRIVATE_CHAT_ROOM);

                    // If message receive is not store into chatRoom then it will create a new one
                    if (chatRoom == null) {
                        chatRoom = new ChatRoom();
                        chatRoom.setStatus(ChatRoom.CHAT_ROOM_JOINED);
                        chatRoom.setChatRoomType(ChatRoom.PRIVATE_CHAT_ROOM);
                        //Because no name that is why is set as sender id
                        chatRoom.setName(mProcessMessage[1]);
                        chatRoom.setDateTimeMessageReceived(mProcessMessage[2]);

                        String[] processUniqueTopic = mProcessMessage[4].split("_");
                        //chatRoom.setChatRoomUniqueTopic(processUniqueTopic[1] + "_" + processUniqueTopic[0]);

                        // Set sender id as unique topic
                        chatRoom.setChatRoomUniqueTopic(mProcessMessage[1]);

                        chatRoom.setLatestMessage(mProcessMessage[0]);
                        applicationDatabase.chatRoomDao().insert(chatRoom);

                    } else {
                        chatRoom.setName(mProcessMessage[1]);
                        chatRoom.setDateTimeMessageReceived(mProcessMessage[2]);
                        chatRoom.setChatRoomUniqueTopic(mProcessMessage[1]);
                        chatRoom.setLatestMessage(mProcessMessage[0]);
                        applicationDatabase.chatRoomDao().updateChatRoom(chatRoom);

                    }


                    chat.setRoomId(chatRoom.getId());
                    applicationDatabase.chatDao().insert(chat);
                } else {
                    chat.setRoomId(chatRoomGroup.getId());
                    applicationDatabase.chatDao().insert(chat);
                }


            }






            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (mWeakReference.get().getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
                    .getString(ChatFragment.CHAT_ENGINE_MESSAGE_RECEIVED, "Nothing")) {
                case ChatFragment.TAG:
                    ChatFragment.refreshPage();
                    Log.i(TAG, "Entered Chat Fragment");
                    break;
                case ChatRoomActivity.TAG:
                    ChatRoomActivity.refreshPage();
                    Log.i(TAG, "Entered Chat Room Activity");
                    break;
            }
        }

    }

    private static class InsertGroupChatRoomAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Activity> mWeakReference;
        private String[] mProcessMessage;

        private InsertGroupChatRoomAsyncTask(Activity activity, String[] processMessage) {
            mWeakReference = new WeakReference<>(activity);
            mProcessMessage = processMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(mWeakReference.get());

            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setChatRoomType(mProcessMessage[0]);
            chatRoom.setChatRoomUniqueTopic(mProcessMessage[1]);
            chatRoom.setDateTimeMessageReceived(mProcessMessage[2]);
            chatRoom.setGroupMember(mProcessMessage[3]);
            chatRoom.setLatestMessage(mProcessMessage[4]);
            chatRoom.setName(mProcessMessage[5]);
            chatRoom.setStatus(mProcessMessage[6]);


            applicationDatabase.chatRoomDao().insert(chatRoom);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (mWeakReference.get().getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
                    .getString(ChatFragment.CHAT_ENGINE_MESSAGE_RECEIVED, "Nothing")) {
                case ChatFragment.TAG:
                    ChatFragment.refreshPage();
                    Log.i(TAG, "Entered Chat Fragment");
                    break;
                case ChatRoomActivity.TAG:
                    ChatRoomActivity.refreshPage();
                    Log.i(TAG, "Entered Chat Room Activity");
                    break;
            }

            Log.i(TAG, "CHatEngine");
            new ChatEngineStartup(mWeakReference.get()).execute();
        }

    }

}
