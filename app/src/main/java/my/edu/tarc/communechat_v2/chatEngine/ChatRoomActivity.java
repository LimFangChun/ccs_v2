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
    private List<Chat> mChatList;
    private RecyclerView mRecyclerView;
    private long mChatRoomIdLong;
    private String mChatRoomUniqueTopic = "0";

    private long mUserContactId;
    //private long mRecipientContactId;
    public static final String TAG = "ChatRoomActivity";
    private static WeakReference<ChatRoomActivity> mWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: replace it with the real actual id
        mUserContactId = getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
                .getInt(ChatFragment.CHAT_ENGINE_USER_ID, 0);
        // TODO: get recipient contact id
        //mRecipientContactId = getIntent().getLongExtra();

        //This prevent the loop when creating new chat room
        SelectContactActivity.sChatRoomId = -1;

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

        mWeakReference = new WeakReference<>(this);

    }

    public static void refreshPage() {
        Log.i(TAG, "Refreshing Page");
        mWeakReference.get().onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ce_chat_room_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ce_menu_chatRoom_search:
                //TODO TEMP
                //MainActivity.mqttHelper.subscribe(mUserContactId + "_" + mRecipientContactId);
                //MainActivity.mqttHelper.subscribe( mRecipientContactId + "_" + mUserContactId);
                // Qos = 1 means guarantee send message

                //Intent intent = new Intent(this, AddGroupActivity.class);
                //startActivity(intent);
                break;
            case R.id.ce_menu_chatRoom_check:
                // TODO: This part must be called at the beginning of the app
                // It will notify the broker to retrieve message
                //MainActivity.mqttHelper.getMqttClient().setCallback(new ChatSubscribeCallBack(this));
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Inform Which page is the user currently at
        getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
                .edit().putString(ChatFragment.CHAT_ENGINE_MESSAGE_RECEIVED, TAG).apply();

        new StartupAsyncTask(this).execute();
    }

    @Override
    public void onClick(View view) {

        if (!mMessageBoxEditText.getText().toString().equals("")) {
            //FIXME: here stuck
            Chat chat = new Chat();
            chat.setDate(new MyDateTime().getDateTime());
            chat.setMessage(mMessageBoxEditText.getText().toString());
            chat.setMessageType(Chat.TEXT_MESSAGE);
            chat.setRoomId(mChatRoomIdLong);

            // This part put your user id
            chat.setSenderId(String.valueOf(mUserContactId));
            chat.setChatRoomUniqueTopic(mChatRoomUniqueTopic);

            String[] processUniqueTopic = mChatRoomUniqueTopic.split("_");
            //chat.setChatRoomUniqueTopic(processUniqueTopic[1] + "_" + processUniqueTopic[0]);

            //Log.i(TAG, "MUST SEE" + mChatRoomUniqueTopic);
            //Log.i(TAG, "MUST SEE" + processUniqueTopic[1] + "_" + processUniqueTopic[0]);

            //String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
            //TODO: CE Place Chat Room Id

            if (isNetworkAvailable()) {

                MainActivity.mqttHelper.connectPublishSubscribe(this,
                        mChatRoomUniqueTopic,
                        MqttHeader.SEND_MESSAGE, chat
                );

                new UpdateAsyncTask(this, chat).execute();
                mMessageBoxEditText.setText("");

            }


        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private static WeakReference<ChatRoomActivity> mWeakReference;
        private List<Chat> mChatList;
        private ActionCallBackAdapter mActionCallBackAdapter;

        private RecyclerViewAdapter(List<Chat> chatList, ChatRoomActivity chatRoomFragment) {
            mChatList = chatList;
            mWeakReference = new WeakReference<>(chatRoomFragment);
            mActionCallBackAdapter = new ActionCallBackAdapter(
                    mWeakReference.get(), this, mWeakReference.get().mRecyclerView
            );
        }

        protected class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {

            private ImageView mIconImageView;
            private TextView mSenderLeftTextView, mUserRightTextView;
            private TextView mSenderLeftTimeTextView, mUserRightTimeTextView;

            private ConstraintLayout mConstraintLayout;

            private ViewHolder(View itemView) {
                super(itemView);

                mUserRightTextView = itemView.findViewById(R.id.textView_itemChatRoom_userRight);
                mSenderLeftTextView = itemView.findViewById(R.id.textView_itemChatRoom_senderLeft);
                mConstraintLayout = itemView.findViewById(R.id.layout);
                mSenderLeftTimeTextView = itemView.findViewById(R.id.textView_itemChatRoom_senderLeftTime);
                mUserRightTimeTextView = itemView.findViewById(R.id.textView_itemChatRoom_userRightTime);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();

                Chat chat = mChatList.get(position);

                if (mActionCallBackAdapter.checkActionModeCallBackOpen()) {

                    if (mActionCallBackAdapter.setSelectItem(chat, position)) {
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
                    mWeakReference.get().startActionMode(
                            mActionCallBackAdapter
                    );
                    mActionCallBackAdapter.openActionModeCallBack();
                }


                Chat chat = mChatList.get(position);

                if (mActionCallBackAdapter.setSelectItem(chat, position)) {
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

            if (chat.getSenderId().equals(String.valueOf(mWeakReference.get().mUserContactId))) {
                holder.mUserRightTextView.setVisibility(View.VISIBLE);
                holder.mUserRightTextView.setText(chat.getMessage());
                holder.mUserRightTimeTextView.setVisibility(View.VISIBLE);
                holder.mUserRightTimeTextView.setText(MyDateTime.getTime(chat.getDate()));

                holder.mSenderLeftTimeTextView.setVisibility(View.GONE);
                holder.mSenderLeftTextView.setVisibility(View.GONE);


            } else {
                holder.mSenderLeftTextView.setText(chat.getMessage());
                holder.mSenderLeftTextView.setVisibility(View.VISIBLE);
                holder.mSenderLeftTimeTextView.setText(MyDateTime.getTime(chat.getDate()));
                holder.mSenderLeftTimeTextView.setVisibility(View.VISIBLE);


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

        private WeakReference<ChatRoomActivity> mWeakReference;
        private boolean mIsActionModeCallBackOpen = false;
        private boolean mIsSelectedChatDeleted = false;
        private ActionMode mActionMode;
        private RecyclerViewAdapter mRecyclerViewAdapter;
        private RecyclerView mRecyclerView;
        private List<Integer> mPositionIntegerList = new ArrayList<>();
        private List<Chat> mSelectedChatList = new ArrayList<>();

        private ActionCallBackAdapter(ChatRoomActivity chatRoomActivity,
                                      RecyclerViewAdapter recyclerViewAdapter,
                                      RecyclerView recyclerView) {
            mWeakReference = new WeakReference<>(chatRoomActivity);
            mRecyclerViewAdapter = recyclerViewAdapter;
            mRecyclerView = recyclerView;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            mWeakReference.get().getMenuInflater()
                    .inflate(R.menu.context_menu, menu);

            mActionMode = actionMode;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            mIsSelectedChatDeleted = true;
            new DeleteAsyncTask(mSelectedChatList, actionMode, mWeakReference.get()).execute();
            return true;

        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {


            mRecyclerViewAdapter.notifyDataSetChanged();
            mSelectedChatList.clear();
            mPositionIntegerList.clear();
            mIsSelectedChatDeleted = false;
            mIsActionModeCallBackOpen = false;
            actionMode.finish();
            mWeakReference.get().onPostResume();
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
        public boolean setSelectItem(Chat chat, int itemPosition) {
            boolean returnValue;

            if (mSelectedChatList.contains(chat)) {
                mPositionIntegerList.remove(Integer.valueOf(itemPosition));
                mSelectedChatList.remove(chat);
                if (mSelectedChatList.isEmpty()) {
                    onDestroyActionMode(mActionMode);
                } else {
                    mActionMode.setTitle(String.valueOf(mSelectedChatList.size()));
                }
                returnValue = false;

            } else {
                mPositionIntegerList.add(itemPosition);
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

        private WeakReference<ChatRoomActivity> mWeakReference;

        private StartupAsyncTask(ChatRoomActivity chatRoomActivity) {
            mWeakReference = new WeakReference<>(chatRoomActivity);
        }

        @Override
        protected List<Chat> doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(mWeakReference.get());


            return applicationDatabase.chatDao()
                    .getChatFromChatRoom(mWeakReference.get().mChatRoomIdLong);
        }

        @Override
        protected void onPostExecute(List<Chat> chatList) {
            super.onPostExecute(chatList);

            // FIXME: this place got bug every time start instant run
            try {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        mWeakReference.get(), LinearLayoutManager.VERTICAL, false
                );
                linearLayoutManager.setStackFromEnd(true);
                mWeakReference.get().mRecyclerView.setLayoutManager(linearLayoutManager);
                mWeakReference.get().mRecyclerView.setHasFixedSize(true);

                mWeakReference.get().mRecyclerView.setAdapter(
                        new RecyclerViewAdapter(chatList, mWeakReference.get()));

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

    private static class UpdateAsyncTask extends AsyncTask<Void,Void,Void> {

        private WeakReference<ChatRoomActivity> mWeakReference;
        private Chat mChat;

        private UpdateAsyncTask(ChatRoomActivity chatRoomActivity, Chat chat) {
            mWeakReference = new WeakReference<>(chatRoomActivity);
            mChat = chat;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(mWeakReference.get());

            applicationDatabase.chatDao().insert(mChat);

            //This call the chatRoom data for update
            ChatRoom chatRoom = applicationDatabase.chatRoomDao()
                    .searchExistingChatRoom(mWeakReference.get().mChatRoomIdLong);

            chatRoom.setLatestMessage(mChat.getMessage());
            chatRoom.setDateTimeMessageReceived(mChat.getDate());

            applicationDatabase.chatRoomDao().updateChatRoom(chatRoom);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Refer back to the resume part to reload the data from database
            mWeakReference.get().onPostResume();

        }

    }

    private static class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<ChatRoomActivity> mWeakReference;
        private ActionMode mActionMode;
        private List<Chat> mChatList;

        private DeleteAsyncTask(List<Chat> chatList, ActionMode actionMode,
                                ChatRoomActivity chatRoomActivity) {
            mWeakReference = new WeakReference<>(chatRoomActivity);
            mActionMode = actionMode;
            mChatList = chatList;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase =
                    ApplicationDatabase.build(mWeakReference.get());

            // Remove chat
            applicationDatabase.chatDao().deleteAllSelectedChat(mChatList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWeakReference.get().onPostResume();
            mActionMode.finish();
        }
    }

}
