package my.edu.tarc.communechat_v2.chatEngine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.chatEngine.database.ApplicationDatabase;
import my.edu.tarc.communechat_v2.chatEngine.database.Chat;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;
import my.edu.tarc.communechat_v2.chatEngine.testData.TestData;
import my.edu.tarc.communechat_v2.internal.MqttHeader;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mMessageBoxEditText;
    private RecyclerView mRecyclerView;
    private long mChatRoomIdLong;
    private String mChatRoomUniqueTopic = "0";

    private String mGroupStatus;

    private long mUserContactId;
    //private long mRecipientContactId;
    public static final String TAG = "ChatRoomActivity";

    private static WeakReference<ChatRoomActivity> sWeakReference;
    private boolean mIsThisAGroupChatRoom = false;

    private MenuItem mGroupInfoMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //TODO: replace it with the real actual id
        mUserContactId = getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
                .getInt(ChatFragment.CHAT_ENGINE_USER_ID, 0);



        // Check whether action bar is initialize
        if (getSupportActionBar() != null) {
            // Set return back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mChatRoomIdLong = getIntent().getLongExtra(ChatFragment.SELECTED_CHAT_ROOM_ID, -1);
        mChatRoomUniqueTopic = getIntent().getStringExtra(ChatFragment.SELECTED_CHAT_ROOM_UNIQUE_TOPIC);

        Button sendButton = findViewById(R.id.button_chatRoomActivity_send);
        sendButton.setOnClickListener(this);

        mMessageBoxEditText = findViewById(R.id.editText_chatRoomActivity_inputBox);

        mRecyclerView = findViewById(R.id.recyclerView_chatRoomActivity);

        sWeakReference = new WeakReference<>(this);


    }

    public static void refreshPage() {
        Log.i(TAG, "Refreshing Page");
        sWeakReference.get().onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ce_chat_room_menu, menu);
        mGroupInfoMenuItem = menu.findItem(R.id.ce_menu_chatRoom_check);
        //Only reveal the group info button in the action bar when this chat room is a group room
        mGroupInfoMenuItem.setVisible(mIsThisAGroupChatRoom);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ce_menu_chatRoom_search:
                //TODO If there's time do a search function here
                break;
            case R.id.ce_menu_chatRoom_check:
                //This part call the group management activity
                //Only group chat room can access to group management
                if (mIsThisAGroupChatRoom) {
                    Intent intent = new Intent(this, GroupManagementActivity.class);
                    intent.putExtra(GroupManagementActivity.CHAT_ROOM_TOPIC, mChatRoomUniqueTopic);
                    startActivity(intent);
                }

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Inform Which page is the user currently at
        getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
                .edit().putString(ChatFragment.CHAT_ENGINE_MESSAGE_RECEIVED, TAG).apply();

        new StartupAsyncTask().execute();
    }

    @Override
    public void onClick(View view) {


        if (!mMessageBoxEditText.getText().toString().equals("")) {

            MyDateTime myDateTime = new MyDateTime();

            Chat chat = new Chat();
            chat.setDate(myDateTime.getDateTime());
            chat.setMessage(mMessageBoxEditText.getText().toString());
            chat.setMessageType(Chat.TEXT_MESSAGE);

            // This part put your user id
            chat.setSenderId(String.valueOf(mUserContactId));
            chat.setChatRoomUniqueTopic(mChatRoomUniqueTopic);
            chat.setRoomId(mChatRoomIdLong);
            chat.setComparingDateTime(String.valueOf(myDateTime.getCurrentTimeInMillisecond()));

            if (isNetworkAvailable()) {

                MainActivity.mqttHelper.publish(
                        mChatRoomUniqueTopic,
                        MqttHeader.SEND_MESSAGE, chat
                );

                new UpdateAsyncTask(this, chat, mChatRoomIdLong).execute();
                mMessageBoxEditText.setText("");

            } //TODO add alert dialog


        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private static long sSizeOfChat = 0;
        private List<Chat> mChatList;
        private ActionCallBackAdapter mActionCallBackAdapter;

        private RecyclerViewAdapter(List<Chat> chatList) {
            mChatList = chatList;
            sSizeOfChat = chatList.size();
            mActionCallBackAdapter = new ActionCallBackAdapter(this);
        }

        protected class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {

            private ImageView mIconImageView;
            private TextView mSenderLeftTextView, mUserRightTextView;
            private TextView mSenderLeftTimeTextView, mUserRightTimeTextView;
            private TextView mSenderLeftNameTextView;

            private ConstraintLayout mConstraintLayout;

            private ViewHolder(View itemView) {
                super(itemView);

                mUserRightTextView = itemView.findViewById(R.id.textView_itemChatRoom_userRight);
                mSenderLeftTextView = itemView.findViewById(R.id.textView_itemChatRoom_senderLeft);

                mConstraintLayout = itemView.findViewById(R.id.layout);

                mSenderLeftTimeTextView = itemView.findViewById(R.id.textView_itemChatRoom_senderLeftTime);
                mUserRightTimeTextView = itemView.findViewById(R.id.textView_itemChatRoom_userRightTime);

                mSenderLeftNameTextView = itemView.findViewById(R.id.textView_itemChatRoom_senderLeftName);

                mUserRightTextView.setBackgroundColor(Color.GRAY);
                mUserRightTimeTextView.setBackgroundColor(Color.GRAY);

                mSenderLeftNameTextView.setBackgroundColor(sWeakReference.get().getResources().getColor(R.color.colorPrimary));
                mSenderLeftTimeTextView.setBackgroundColor(sWeakReference.get().getResources().getColor(R.color.colorPrimary));
                mSenderLeftTextView.setBackgroundColor(sWeakReference.get().getResources().getColor(R.color.colorPrimary));

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();

                Chat chat = mChatList.get(position);

                if (mActionCallBackAdapter.checkActionModeCallBackOpen()) {

                    if (mActionCallBackAdapter.setSelectItem(chat)) {
                        mConstraintLayout.setBackgroundColor(Color.WHITE);
                    } else {
                        mConstraintLayout.setBackgroundColor(Color.TRANSPARENT);
                    }
                }

            }

            @Override
            public boolean onLongClick(View view) {
                int position = getAdapterPosition();

                if (!mActionCallBackAdapter.checkActionModeCallBackOpen()) {
                    sWeakReference.get().startActionMode(
                            mActionCallBackAdapter
                    );
                    mActionCallBackAdapter.openActionModeCallBack();
                }


                Chat chat = mChatList.get(position);

                if (mActionCallBackAdapter.setSelectItem(chat)) {
                    mConstraintLayout.setBackgroundColor(Color.WHITE);
                } else {
                    mConstraintLayout.setBackgroundColor(Color.TRANSPARENT);
                }


                return true;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ce_item_chat_room, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Chat chat = mChatList.get(position);

            if (mActionCallBackAdapter.mSelectedChatList.contains(chat)) {
                holder.mConstraintLayout.setBackgroundColor(Color.WHITE);
            } else {
                holder.mConstraintLayout.setBackgroundColor(Color.TRANSPARENT);
            }

            String secondFormat;
            // This part add 0 in front of the minute if value is less than 10
            if (Integer.parseInt(MyDateTime.getTime(chat.getDate()).split(":")[1]) > 9) {
                secondFormat = MyDateTime.getTime(chat.getDate()).split(":")[1];
            } else {
                secondFormat = "0" + MyDateTime.getTime(chat.getDate()).split(":")[1];
            }

            String displayTime = MyDateTime.getTime(chat.getDate()).split(":")[0] + ":" + secondFormat;

            if (chat.getSenderId().equals(String.valueOf(sWeakReference.get().mUserContactId))) {
                holder.mUserRightTextView.setVisibility(View.VISIBLE);
                holder.mUserRightTextView.setText(chat.getMessage());
                holder.mUserRightTimeTextView.setVisibility(View.VISIBLE);
                holder.mUserRightTimeTextView.setText(displayTime);

                holder.mSenderLeftTimeTextView.setVisibility(View.GONE);
                holder.mSenderLeftTextView.setVisibility(View.GONE);
                holder.mSenderLeftNameTextView.setVisibility(View.GONE);


            } else {
                holder.mSenderLeftTextView.setText(chat.getMessage());
                holder.mSenderLeftTextView.setVisibility(View.VISIBLE);
                holder.mSenderLeftTimeTextView.setText(displayTime);
                holder.mSenderLeftTimeTextView.setVisibility(View.VISIBLE);
                //Check whether message is a group message

                if (sWeakReference.get().mIsThisAGroupChatRoom) {
                    holder.mSenderLeftNameTextView.setText(chat.getSenderId());
                    holder.mSenderLeftNameTextView.setVisibility(View.VISIBLE);
                }

                holder.mUserRightTextView.setVisibility(View.GONE);
                holder.mUserRightTimeTextView.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return mChatList.size();
        }

    }

    private static class ActionCallBackAdapter implements ActionMode.Callback {

        private boolean mIsActionModeCallBackOpen = false;
        private ActionMode mActionMode;
        private RecyclerViewAdapter mRecyclerViewAdapter;
        private List<Chat> mSelectedChatList = new ArrayList<>();

        private ActionCallBackAdapter(RecyclerViewAdapter recyclerViewAdapter) {
            mRecyclerViewAdapter = recyclerViewAdapter;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            sWeakReference.get().getMenuInflater().inflate(R.menu.context_menu, menu);
            mActionMode = actionMode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            new DeleteAsyncTask(mSelectedChatList, actionMode, sWeakReference.get()).execute();
            return true;

        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {


            mRecyclerViewAdapter.notifyDataSetChanged();
            mSelectedChatList.clear();
            mIsActionModeCallBackOpen = false;
            actionMode.finish();
            sWeakReference.get().onResume();
        }

        public boolean checkActionModeCallBackOpen() {
            return mIsActionModeCallBackOpen;
        }

        public void openActionModeCallBack() {
            mIsActionModeCallBackOpen = true;
        }

        /**
         * The call will return true when item is added and false when item is remove
         **/
        public boolean setSelectItem(Chat chat) {
            boolean returnValue;

            if (mSelectedChatList.contains(chat)) {
                mSelectedChatList.remove(chat);
                if (mSelectedChatList.isEmpty()) {
                    onDestroyActionMode(mActionMode);
                } else {
                    mActionMode.setTitle(String.valueOf(mSelectedChatList.size()));
                }
                returnValue = false;

            } else {
                mSelectedChatList.add(chat);
                mActionMode.setTitle(String.valueOf(mSelectedChatList.size()));
                returnValue = true;
            }

            return returnValue;
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static class StartupAsyncTask extends AsyncTask<Void,Void,List<Chat>> {

        private ChatRoom mChatRoom;

        private StartupAsyncTask() {
        }

        @Override
        protected List<Chat> doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(sWeakReference.get());

            Log.i(TAG,sWeakReference.get().mChatRoomUniqueTopic + "Topic" );

            mChatRoom = applicationDatabase.chatRoomDao().get(sWeakReference.get().mChatRoomUniqueTopic);

            Log.i(TAG, (mChatRoom == null) + "");

            //Inform the chat room that is open is a group or private
            sWeakReference.get().mIsThisAGroupChatRoom =
                    mChatRoom.getChatRoomType().equals(ChatRoom.GROUP_CHAT_ROOM);

            sWeakReference.get().mGroupStatus = mChatRoom.getStatus();

            return applicationDatabase.chatDao()
                    .getChatFromChatRoom(sWeakReference.get().mChatRoomIdLong);
        }

        @Override
        protected void onPostExecute(List<Chat> chatList) {
            super.onPostExecute(chatList);

            if (mChatRoom.getChatRoomType().equals(ChatRoom.GROUP_CHAT_ROOM)) {
                if (sWeakReference.get().mGroupStatus.equals(ChatRoom.CHAT_ROOM_JOINED)) {
                    sWeakReference.get().mMessageBoxEditText.setFocusable(true);
                    sWeakReference.get().mMessageBoxEditText.setEnabled(true);
                    sWeakReference.get().mMessageBoxEditText.setCursorVisible(true);
                } else {
                    sWeakReference.get().mMessageBoxEditText.setFocusable(false);
                    sWeakReference.get().mMessageBoxEditText.setEnabled(false);
                    sWeakReference.get().mMessageBoxEditText.setCursorVisible(false);

                }
            }

            try {
                sWeakReference.get().setTitle(mChatRoom.getName());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        sWeakReference.get(), LinearLayoutManager.VERTICAL, true
                );
                linearLayoutManager.setStackFromEnd(false);
                sWeakReference.get().mRecyclerView.setLayoutManager(linearLayoutManager);
                sWeakReference.get().mRecyclerView.setHasFixedSize(true);



                sWeakReference.get().mRecyclerView.setAdapter(
                        new RecyclerViewAdapter(chatList));

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

    public static class UpdateAsyncTask extends AsyncTask<Void,Void,Void> {

        private Chat mChat;
        private long mChatRoomIdLong;

        /*
         *   Would have used weak reference but one part in notification can only provide context
        */
        
        private Context mContext;

        public UpdateAsyncTask(Context context, Chat chat, long chatRoomLongId) {

            mContext = context;
            mChat = chat;
            mChatRoomIdLong = chatRoomLongId;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(mContext);

            applicationDatabase.chatDao().insert(mChat);

            //This call the chatRoom data for update
            ChatRoom chatRoom = applicationDatabase.chatRoomDao()
                    .get(mChatRoomIdLong);

            chatRoom.setLatestMessage(mChat.getMessage());
            chatRoom.setDateTimeMessageReceived(mChat.getDate());
            chatRoom.setComparingDateTime(mChat.getComparingDateTime());

            applicationDatabase.chatRoomDao().updateChatRoom(chatRoom);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Refer back to the resume part to reload the data from database
            switch (mContext.getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
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

    private static class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<ChatRoomActivity> mWeakReference;
        private ActionMode mActionMode;
        private List<Chat> mChatList = new ArrayList<>();

        private DeleteAsyncTask(List<Chat> chatList, ActionMode actionMode,
                                ChatRoomActivity chatRoomActivity) {
            mWeakReference = new WeakReference<>(chatRoomActivity);
            mActionMode = actionMode;
            mChatList.addAll(chatList);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase =
                    ApplicationDatabase.build(mWeakReference.get());

            ChatRoom chatRoom = applicationDatabase.chatRoomDao().get(mWeakReference.get().mChatRoomUniqueTopic);
            // Check if user wish to delete all the chat
            if (RecyclerViewAdapter.sSizeOfChat == mChatList.size()) {
                chatRoom.setLatestMessage("");
                chatRoom.setDateTimeMessageReceived("");
            } else {
                List<Chat> chatList = applicationDatabase.chatDao().getChatFromChatRoom(mWeakReference.get().mChatRoomIdLong);
                chatRoom.setLatestMessage(chatList.get(chatList.size()-1).getMessage());
                chatRoom.setDateTimeMessageReceived(chatList.get(chatList.size()-1).getDate());
            }

            applicationDatabase.chatRoomDao().updateChatRoom(chatRoom);

            // Remove chat
            applicationDatabase.chatDao().deleteAllSelectedChat(mChatList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWeakReference.get().onResume();
            mActionMode.finish();
        }
    }

}
