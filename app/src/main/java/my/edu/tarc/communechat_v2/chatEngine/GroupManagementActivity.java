package my.edu.tarc.communechat_v2.chatEngine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.chatEngine.database.ApplicationDatabase;
import my.edu.tarc.communechat_v2.chatEngine.database.Chat;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.User;

public class GroupManagementActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mNameTextView, mSizeTextView;
    private EditText mNameEditText;
    private RecyclerView mMemberRecyclerView;


    private boolean mIsGroupJoined = false;

    private String mChatRoomUniqueTopic = "";
    private String mChatRoomName = "";

    private static WeakReference<GroupManagementActivity> sWeakReference;

    private boolean isAdmin = false;

    public static final String CHAT_ROOM_TOPIC = "ChatRoomTopic";

    public static final String TAG = "GroupManagementActivity";

    public static final String NEW_MEMBER_DETAIL = "NewMemberDetail";

    public static final String EXISTING_MEMBER_LIST = "ExistingMemberList";

    private String mNewMemberDetail;

    private String mExistingMemberDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sWeakReference = new WeakReference<>(this);

        mChatRoomUniqueTopic = getIntent().getStringExtra(CHAT_ROOM_TOPIC);

        setTitle("Group Info");

        mNameEditText = findViewById(R.id.editText_groupManagementActivity_name);
        mNameTextView = findViewById(R.id.textView_groupManagementActivity_name);
        mSizeTextView = findViewById(R.id.textView_groupManagementActivity_counter);
        mMemberRecyclerView = findViewById(R.id.recyclerView_groupManagementActivity);
        ImageButton mEditImageButton = findViewById(R.id.imageButton_groupManagementActivity_edit);

        mEditImageButton.setOnClickListener(this);

        // Check whether action bar is initialize
        if (getSupportActionBar() != null) {
            // Set return back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        if (mIsGroupJoined) {
            switch (view.getId()) {
                case R.id.imageButton_groupManagementActivity_edit:

                    //If text view is hidden it will reveal it
                    if (mNameTextView.getVisibility() == View.INVISIBLE && !mNameEditText.getText().toString().isEmpty()) {
                        mChatRoomName = mNameEditText.getText().toString();
                        mNameTextView.setText(mChatRoomName);
                        mNameEditText.setVisibility(View.INVISIBLE);
                        mNameTextView.setVisibility(View.VISIBLE);
                        new UpdateAsyncTask(UpdateAsyncTask.CHANGE_GROUP_NAME).execute();
                    } else {
                        mNameEditText.setText(mChatRoomName);
                        mNameEditText.setVisibility(View.VISIBLE);
                        mNameTextView.setVisibility(View.INVISIBLE);
                    }


                    break;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.ce_chat_room_group_management_menu, menu);
        menu.findItem(R.id.ce_menu_groupManagement_disbandGroup).setVisible(isAdmin);
        menu.findItem(R.id.ce_menu_groupManagement_addMember).setVisible(isAdmin);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mIsGroupJoined) {
            switch (item.getItemId()) {
                case R.id.ce_menu_groupManagement_addMember:
                    Intent intent = new Intent(this, SelectContactActivity.class);
                    intent.putExtra(SelectContactActivity.SELECTION_TYPE, SelectContactActivity.SELECT_NEW_GROUP_MEMBER);
                    intent.putExtra(EXISTING_MEMBER_LIST, mExistingMemberDetail);
                    startActivityForResult(intent, 1);
                    break;
                case R.id.ce_menu_groupManagement_leaveGroup:
                    new UpdateAsyncTask(UpdateAsyncTask.LEAVE_GROUP).execute();
                    break;
                case R.id.ce_menu_groupManagement_disbandGroup:
                    new UpdateAsyncTask(UpdateAsyncTask.DISBAND_GROUP).execute();
                    break;

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mNewMemberDetail = data.getStringExtra(NEW_MEMBER_DETAIL);
            new UpdateAsyncTask(UpdateAsyncTask.ADD_MEMBER).execute();
        }


    }

    private static class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private ChatRoom mChatRoom;
        private String[] mMemberInfoList;
        private List<String> mAdminMember;

        private RecyclerViewAdapter(ChatRoom chatRoom) {
            mChatRoom = chatRoom;
            mAdminMember = Arrays.asList(chatRoom.getAdminUserId().split(ChatRoom.GROUP_DIVIDER));
            mMemberInfoList = mChatRoom.getGroupMember().split(ChatRoom.GROUP_DIVIDER);
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView mIconImageView;
            private TextView mMemberIdTextView, mMemberNameTextView, mMemberStatusTextView;

            private ViewHolder(View itemView) {
                super(itemView);

                mMemberIdTextView = itemView.findViewById(R.id.textView_groupMemberItem_id);
                mMemberNameTextView = itemView.findViewById(R.id.textView_groupMemberItem_name);
                mMemberStatusTextView = itemView.findViewById(R.id.textView_groupMemberItem_status);

                if (sWeakReference.get().isAdmin) {
                    itemView.setOnClickListener(this);
                }

            }

            @Override
            public void onClick(View view) {

                final String[] processInfo = mMemberInfoList[getAdapterPosition()].split(ChatRoom.ID_NAME_DIVIDER);
                if (!processInfo[0].equals(String.valueOf(ChatFragment.CURRENT_USER_ID))) {
                    AlertDialog alertDialog = new AlertDialog.Builder(sWeakReference.get()).create();
                    alertDialog.setTitle("What do you wish to do");

                    final String adminActionMessage;

                    if (Arrays.asList(mChatRoom.getAdminUserId().split(ChatRoom.GROUP_DIVIDER)).contains(mMemberInfoList[getAdapterPosition()].split(ChatRoom.ID_NAME_DIVIDER)[0])) {
                        adminActionMessage = "Demote Admin";
                    } else {
                        adminActionMessage = "Promote Admin";
                    }

                    alertDialog.setMessage("What do you wish to do for #" + mMemberIdTextView.getText().toString() + " " + mMemberNameTextView.getText().toString());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, adminActionMessage, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String message;

                            if (adminActionMessage.equals("Demote Admin")) {

                                message = "I have demoted #" + processInfo[0] + " @" + processInfo[1] + " from an admin to a normal member";
                                String[] adminList = mChatRoom.getAdminUserId().split(ChatRoom.GROUP_DIVIDER);
                                StringBuilder stringBuilder = new StringBuilder();

                                // This part remove the selected admin from the admin list
                                for (String adminId : adminList) {
                                    if (!adminId.equals(processInfo[0])) {
                                        stringBuilder.append(adminId).append(ChatRoom.GROUP_DIVIDER);
                                    }

                                }
                                mChatRoom.setAdminUserId(stringBuilder.toString());

                            } else {
                                message = "I have promoted #" + processInfo[0] + " @" + processInfo[1] + " to become an admin";
                                mChatRoom.setAdminUserId(mChatRoom.getAdminUserId() + processInfo[0] + ChatRoom.GROUP_DIVIDER);
                            }

                            //Publish message that a new user is promoted
                            MainActivity.mqttHelper.connectPublishSubscribe(sWeakReference.get(),
                                    mChatRoom.getChatRoomUniqueTopic(),
                                    MqttHeader.ADD_GROUP_CHAT_ROOM, mChatRoom
                            );

                            new UpdateMemberAsyncTask(mChatRoom, message).execute();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Kick Member", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String[] memberArray = mChatRoom.getGroupMember().split(ChatRoom.GROUP_DIVIDER);

                            String lastTopic = mChatRoom.getChatRoomUniqueTopic();

                            StringBuilder stringBuilder = new StringBuilder();

                            //This part remove the user from the group member list
                            for (String member : memberArray) {

                                if (!member.equals(mMemberInfoList[getAdapterPosition()])) {
                                    stringBuilder.append(member)
                                            .append(ChatRoom.GROUP_DIVIDER);
                                }

                            }
                            mChatRoom.setGroupMember(stringBuilder.toString());

                            String[] adminList = mChatRoom.getAdminUserId().split(ChatRoom.GROUP_DIVIDER);
                            StringBuilder adminStringBuilder = new StringBuilder();
                            // This part remove the selected admin from the admin list
                            for (String adminId : adminList) {
                                if (!adminId.equals(processInfo[0])) {
                                    adminStringBuilder.append(adminId)
                                            .append(ChatRoom.GROUP_DIVIDER);
                                }

                            }
                            mChatRoom.setAdminUserId(adminStringBuilder.toString());

                            MainActivity.mqttHelper.connectPublishSubscribe(sWeakReference.get(),
                                    lastTopic,
                                    MqttHeader.ADD_GROUP_CHAT_ROOM, mChatRoom
                            );


                            new UpdateMemberAsyncTask(mChatRoom, "I have kicked #" + processInfo[0] + " @" + processInfo[1]).execute();

                        }
                    });
                    alertDialog.show();
                }


            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ce_item_group_member, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            //This is needed when all group member is left empty during the disband of the group
            if (!mMemberInfoList[position].isEmpty()) {
                String[] processInfo = mMemberInfoList[position].split(ChatRoom.ID_NAME_DIVIDER);


                holder.mMemberIdTextView.setText(processInfo[0]);
                holder.mMemberNameTextView.setText(processInfo[1]);

                if (mAdminMember.contains(processInfo[0])) {
                    holder.mMemberStatusTextView.setVisibility(View.VISIBLE);
                    holder.mMemberStatusTextView.setText("Admin");
                } else {
                    holder.mMemberStatusTextView.setVisibility(View.INVISIBLE);
                    holder.mMemberStatusTextView.setText("");
                }
            }

        }

        @Override
        public int getItemCount() {
            if (mChatRoom.getGroupMember().isEmpty()) {
                return 0;
            } else {
                return mMemberInfoList.length;
            }

        }


    }


    private static class StartupAsyncTask extends AsyncTask<Void, Void, ChatRoom> {


        private StartupAsyncTask() {

        }

        @Override
        protected ChatRoom doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(sWeakReference.get());

            return applicationDatabase.chatRoomDao().get(sWeakReference.get().mChatRoomUniqueTopic);
        }

        @Override
        protected void onPostExecute(ChatRoom chatRoom) {
            super.onPostExecute(chatRoom);

            sWeakReference.get().mExistingMemberDetail = chatRoom.getGroupMember();

            sWeakReference.get().mIsGroupJoined = chatRoom.getStatus().equals(ChatRoom.CHAT_ROOM_JOINED);

            try {
                List<String> adminList = Arrays.asList(chatRoom.getAdminUserId().split(ChatRoom.GROUP_DIVIDER));

                //Check whether user is the admin of this group
                sWeakReference.get().isAdmin = adminList.contains(String.valueOf(ChatFragment.CURRENT_USER_ID));


            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            String[] memberInfoList = chatRoom.getGroupMember().split(ChatRoom.GROUP_DIVIDER);

            sWeakReference.get().mChatRoomName = chatRoom.getName();


            sWeakReference.get().mNameEditText.setText(chatRoom.getName());
            sWeakReference.get().mNameTextView.setText(chatRoom.getName());

            if (chatRoom.getGroupMember().isEmpty()) {
                sWeakReference.get().mSizeTextView.setText("Current member: 0");
            } else {
                sWeakReference.get().mSizeTextView.setText("Current member: " + String.valueOf(memberInfoList.length));
            }

            try {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        sWeakReference.get(), LinearLayoutManager.VERTICAL, false
                );

                sWeakReference.get().mMemberRecyclerView.setLayoutManager(linearLayoutManager);
                sWeakReference.get().mMemberRecyclerView.setHasFixedSize(true);
                sWeakReference.get().mMemberRecyclerView.addItemDecoration(
                        new DividerItemDecoration(
                                sWeakReference.get(), DividerItemDecoration.VERTICAL
                        )
                );

                sWeakReference.get().mMemberRecyclerView.setAdapter(
                        new RecyclerViewAdapter(chatRoom)
                );


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

    private static class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {

        private static final String CHANGE_GROUP_NAME = "ChangeGroupName";
        private static final String LEAVE_GROUP = "LeaveGroup";
        private static final String DISBAND_GROUP = "DisbandGroup";
        private static final String ADD_MEMBER = "AddMember";

        private String mAction;

        private UpdateAsyncTask(String action) {
            mAction = action;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            MyDateTime myDateTime = new MyDateTime();

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(sWeakReference.get());

            ChatRoom chatRoom = applicationDatabase
                    .chatRoomDao().get(sWeakReference.get()
                            .mChatRoomUniqueTopic);

            Chat chat = new Chat();
            chat.setDate(myDateTime.getDateTime());
            chat.setMessageType(Chat.TEXT_MESSAGE);
            chat.setSenderId(String.valueOf(ChatFragment.CURRENT_USER_ID));
            chat.setChatRoomUniqueTopic(chatRoom.getChatRoomUniqueTopic());
            chat.setRoomId(chatRoom.getId());
            chat.setComparingDateTime(String.valueOf(myDateTime.getCurrentTimeInMillisecond()));

            switch (mAction) {
                case ADD_MEMBER:
                    chatRoom.setGroupMember(chatRoom.getGroupMember()
                            + sWeakReference.get().mNewMemberDetail + ChatRoom.GROUP_DIVIDER);
                    String[] processMessage = sWeakReference.get().mNewMemberDetail.split(ChatRoom.GROUP_DIVIDER);
                    StringBuilder messageStringBuilder = new StringBuilder();
                    messageStringBuilder.append("I have added #");

                    //Put data for chat message
                    for (String message : processMessage) {
                        String[] idNameMessage = message.split(ChatRoom.ID_NAME_DIVIDER);
                        messageStringBuilder.append(idNameMessage[0] + " @" + idNameMessage[1] + " ");
                    }

                    chat.setMessage(messageStringBuilder.toString());

                    //Individually publish invitation to each member using their id as topic
                    for (String message : processMessage) {
                        String[] idNameMessage = message.split(ChatRoom.ID_NAME_DIVIDER);
                        //Publish invitation separately
                        MainActivity.mqttHelper.connectPublishSubscribe(sWeakReference.get(),
                                idNameMessage[0],
                                MqttHeader.SEND_MESSAGE, chat
                        );

                        MainActivity.mqttHelper.connectPublishSubscribe(sWeakReference.get(),
                                idNameMessage[0],
                                MqttHeader.ADD_GROUP_CHAT_ROOM, chatRoom
                        );


                    }

                    break;
                case LEAVE_GROUP:

                    String[] adminList = chatRoom.getAdminUserId().split(ChatRoom.GROUP_DIVIDER);
                    // If there is no admin in the group it will disband
                    if(!(adminList.length < 2 && sWeakReference.get().isAdmin)) {
                        StringBuilder adminStringBuilder = new StringBuilder();
                        // This part remove the selected admin from the admin list
                        for (String adminId : adminList) {
                            if (!adminId.equals(String.valueOf(ChatFragment.CURRENT_USER_ID))) {
                                adminStringBuilder.append(adminId)
                                        .append(ChatRoom.GROUP_DIVIDER);
                            }

                        }
                        chatRoom.setAdminUserId(adminStringBuilder.toString());



                        String[] processGroupMember = chatRoom.getGroupMember().split(ChatRoom.GROUP_DIVIDER);
                        StringBuilder stringBuilder = new StringBuilder();

                        for (String groupMember : processGroupMember) {
                            String[] idNameMember = groupMember.split(ChatRoom.ID_NAME_DIVIDER);
                            if (!idNameMember[0].contains(String.valueOf(ChatFragment.CURRENT_USER_ID))) {
                                stringBuilder.append(groupMember)
                                        .append(ChatRoom.GROUP_DIVIDER);
                            }
                        }

                        chatRoom.setGroupMember(stringBuilder.toString());

                        Log.i(TAG, chatRoom.getGroupMember() + "Check Group");
                        Log.i(TAG, chatRoom.getStatus() + "Check Group");


                        chatRoom.setStatus(ChatRoom.CHAT_ROOM_LEFT);


                        chat.setMessage("I have left the group");

                        break;
                    }
                    //There is no break here is by design
                    //If group chat room have no admin, it will go and disband chat room
                case DISBAND_GROUP:
                    chatRoom.setStatus(ChatRoom.CHAT_ROOM_DISBAND);
                    chatRoom.setGroupMember("");
                    chatRoom.setAdminUserId("");
                    chat.setMessage("I have disband this group chat");
                    break;
                case CHANGE_GROUP_NAME:
                    chatRoom.setName(sWeakReference.get().mChatRoomName);
                    chat.setMessage("I have change the name to " + sWeakReference.get().mChatRoomName);
                    break;
            }
            chatRoom.setComparingDateTime(String.valueOf(myDateTime.getCurrentTimeInMillisecond()));
            chat.setComparingDateTime(String.valueOf(myDateTime.getCurrentTimeInMillisecond()));

            applicationDatabase.chatRoomDao().updateChatRoom(chatRoom);
            applicationDatabase.chatDao().insert(chat);

            MainActivity.mqttHelper.connectPublishSubscribe(sWeakReference.get(),
                    chatRoom.getChatRoomUniqueTopic(),
                    MqttHeader.SEND_MESSAGE, chat
            );
            MainActivity.mqttHelper.connectPublishSubscribe(sWeakReference.get(),
                    chatRoom.getChatRoomUniqueTopic(),
                    MqttHeader.ADD_GROUP_CHAT_ROOM, chatRoom
            );


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

    }

    private static class UpdateMemberAsyncTask extends AsyncTask<Void, Void, Void> {

        private ChatRoom mChatRoom;
        private String mMessage;

        private UpdateMemberAsyncTask(ChatRoom chatRoom, String message) {
            mChatRoom = chatRoom;
            mMessage = message;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            MyDateTime myDateTime = new MyDateTime();
            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(sWeakReference.get());

            mChatRoom.setLatestMessage(mMessage);
            mChatRoom.setDateTimeMessageReceived(new MyDateTime().getDateTime());
            mChatRoom.setComparingDateTime(String.valueOf(myDateTime.getCurrentTimeInMillisecond()));
            applicationDatabase.chatRoomDao().updateChatRoom(mChatRoom);


            Chat chat = new Chat();
            chat.setDate(myDateTime.getDateTime());
            chat.setMessageType(Chat.TEXT_MESSAGE);
            chat.setSenderId(String.valueOf(ChatFragment.CURRENT_USER_ID));
            chat.setChatRoomUniqueTopic(mChatRoom.getChatRoomUniqueTopic());
            chat.setRoomId(mChatRoom.getId());
            chat.setMessage(mMessage);
            chat.setComparingDateTime(String.valueOf(myDateTime.getCurrentTimeInMillisecond()));
            applicationDatabase.chatDao().insert(chat);

            MainActivity.mqttHelper.connectPublishSubscribe(sWeakReference.get(),
                    mChatRoom.getChatRoomUniqueTopic(),
                    MqttHeader.SEND_MESSAGE, chat
            );

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sWeakReference.get().onResume();
        }

    }

    public static void refreshPage() {
        sWeakReference.get().onResume();
    }

}
