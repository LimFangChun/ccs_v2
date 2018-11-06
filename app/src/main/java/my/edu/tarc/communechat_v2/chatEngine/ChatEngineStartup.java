package my.edu.tarc.communechat_v2.chatEngine;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.chatEngine.database.ApplicationDatabase;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;

public class ChatEngineStartup extends AsyncTask<Void,Void,Void> {

    private WeakReference<Activity> mWeakReference;
    public ChatEngineStartup(Activity activity) {
        mWeakReference = new WeakReference<>(activity);
    }


    @Override
    protected Void doInBackground(Void... voids) {

        ApplicationDatabase applicationDatabase = ApplicationDatabase.build(mWeakReference.get());

        // Get a list to unsubscribe
        List<ChatRoom> unSubscribeChatRoomList = applicationDatabase.chatRoomDao().getUnsubscriptionChatRoom(ChatRoom.PRIVATE_CHAT_ROOM, ChatRoom.CHAT_ROOM_JOINED);
        for (int i = 0; i < unSubscribeChatRoomList.size(); i++) {
            //Only Group Chat Room Require Separate Calculation
            MainActivity.mqttHelper.unsubscribe(unSubscribeChatRoomList.get(i).getChatRoomUniqueTopic());
        }

        // Get list of topic needed to subscribe
        List<ChatRoom> chatRoomList = applicationDatabase.chatRoomDao().getSubscriptionChatRoom(ChatRoom.GROUP_CHAT_ROOM, ChatRoom.CHAT_ROOM_JOINED);


        MainActivity.mqttHelper.subscribe(ChatFragment.CURRENT_USER_ID +"");

        for (int i = 0; i < chatRoomList.size(); i++) {
            //Only Group Chat Room Require Separate Calculation
            if (!chatRoomList.get(i).getChatRoomUniqueTopic().equals(String.valueOf(ChatFragment.CURRENT_USER_ID))) {
                MainActivity.mqttHelper.subscribe(chatRoomList.get(i).getChatRoomUniqueTopic());
                Log.i("CHECKER ENTERED",chatRoomList.get(i).getChatRoomUniqueTopic() );
            }
            Log.i("CHECKER",chatRoomList.get(i).getChatRoomUniqueTopic() );

        }


        return null;
    }
}
