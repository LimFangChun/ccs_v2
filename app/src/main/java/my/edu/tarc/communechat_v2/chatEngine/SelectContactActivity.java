package my.edu.tarc.communechat_v2.chatEngine;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.chatEngine.database.ApplicationDatabase;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.User;

public class SelectContactActivity extends AppCompatActivity {

    public static final String SELECTION_TYPE = "SelectionType";
    public static final int SELECT_PRIVATE_CHAT_MEMBER = 1;
    public static final int SELECT_GROUP_CHAT_MEMBER = 2;
    public static final int SELECT_NEW_GROUP_MEMBER = 3;

    public static final int GET_GROUP_NAME_REQUEST = 12345;

    public static long sChatRoomId = -1;
    public static String sChatRoomUniqueTopic = "";
    public static String sChatRoomName = "";

    public static final String SELECTED_GROUP_MEMBER_USER_ID = "SelectedGroupMemberUserId";


    private RecyclerView mRecyclerView;
    private int mSelectionAction;
    private MenuItem mMenuAddButton;

    private List<String> mSelectedUserIdStringList;
    private String mProcessSelectedUserIdString = "EMPTY";

    private List<String> mSelectedUserNameStringList;
    private static WeakReference<SelectContactActivity> sWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sWeakReference = new WeakReference<>(this);

        mSelectedUserIdStringList = new ArrayList<>();
        mSelectedUserNameStringList = new ArrayList<>();

        mSelectionAction = getIntent().getIntExtra(SELECTION_TYPE, 0);

        mRecyclerView = findViewById(R.id.recyclerView_selectContactActivity);

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
        new StartupAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ce_menu_contact_selection, menu);
        mMenuAddButton = menu.getItem(0);
        mMenuAddButton.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.ce_menu_contact_selection_add) {
            StringBuilder stringBuilder = new StringBuilder();
            if (mSelectionAction == SELECT_NEW_GROUP_MEMBER) {
                //Add member into the list
                for (int i = 0; i < mSelectedUserIdStringList.size(); i++) {
                    stringBuilder.append(mSelectedUserIdStringList.get(i))
                            .append(ChatRoom.ID_NAME_DIVIDER)
                            .append(mSelectedUserNameStringList.get(i))
                            .append(ChatRoom.GROUP_DIVIDER);
                }

                mProcessSelectedUserIdString = stringBuilder.toString();

                Intent intent = new Intent();
                intent.putExtra(GroupManagementActivity.NEW_MEMBER_DETAIL, mProcessSelectedUserIdString);
                setResult(RESULT_OK, intent);
                finish();
            } else {

                //Note: Add Group Chat
                //TODO: Missing username
                // Add Admin into member list
                stringBuilder.append(ChatFragment.CURRENT_USER_ID)
                        .append(ChatRoom.ID_NAME_DIVIDER)
                        .append("Current User Name")
                        .append(ChatRoom.GROUP_DIVIDER);

                //Add member into the list
                for (int i = 0; i < mSelectedUserIdStringList.size(); i++) {
                    stringBuilder.append(mSelectedUserIdStringList.get(i))
                            .append(ChatRoom.ID_NAME_DIVIDER)
                            .append(mSelectedUserNameStringList.get(i))
                            .append(ChatRoom.GROUP_DIVIDER);
                }

                mProcessSelectedUserIdString = stringBuilder.toString();

                Intent intent = new Intent(this, AddGroupActivity.class);
                intent.putExtra(SELECTED_GROUP_MEMBER_USER_ID, mProcessSelectedUserIdString);

                startActivityForResult(intent, GET_GROUP_NAME_REQUEST);

            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check Which Request
        if (requestCode == GET_GROUP_NAME_REQUEST) {

            if (resultCode == RESULT_OK) {
                String chatRoomGroupUniqueTopic = ChatFragment.CURRENT_USER_ID + "_" + (new MyDateTime().getDateTime());

                MyDateTime myDateTime = new MyDateTime();

                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setStatus(ChatRoom.CHAT_ROOM_JOINED);
                chatRoom.setChatRoomType(ChatRoom.GROUP_CHAT_ROOM);
                chatRoom.setName(data.getStringExtra(AddGroupActivity.GROUP_NAME));
                chatRoom.setDateTimeMessageReceived(myDateTime.getDateTime());
                chatRoom.setChatRoomUniqueTopic(chatRoomGroupUniqueTopic);
                chatRoom.setLatestMessage("");
                chatRoom.setAdminUserId(String.valueOf(ChatFragment.CURRENT_USER_ID) + ChatRoom.GROUP_DIVIDER);
                chatRoom.setComparingDateTime(String.valueOf(myDateTime.getCurrentTimeInMillisecond()));
                if (mProcessSelectedUserIdString.equals("EMPTY")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < mSelectedUserIdStringList.size(); i++) {
                        stringBuilder.append(mSelectedUserIdStringList.get(i))
                                .append(ChatRoom.ID_NAME_DIVIDER)
                                .append(mSelectedUserNameStringList.get(i))
                                .append(ChatRoom.GROUP_DIVIDER);
                    }
                    mProcessSelectedUserIdString = stringBuilder.toString();
                }

                chatRoom.setGroupMember(mProcessSelectedUserIdString);

                //Publish to every group member in the group that is invited using their User Id as a Unique Topic
                for (int i = 0; i < mSelectedUserIdStringList.size(); i++) {
                    MainActivity.mqttHelper.connectPublishSubscribe(this,
                            mSelectedUserIdStringList.get(i),
                            MqttHeader.ADD_GROUP_CHAT_ROOM, chatRoom
                    );
                }

                new InsertAsyncTask(chatRoom).execute();
            }

        }

    }

    private static class StartupAsyncTask extends AsyncTask<Void, Void, List<User>> {


        private StartupAsyncTask() {

        }

        @Override
        protected List<User> doInBackground(Void... voids) {

            //TODO: Need to add method to prevent from adding existing member into group
            /*String[] existingGroupMember = sWeakReference.get().getIntent().getStringExtra(
                    GroupManagementActivity.EXISTING_MEMBER_LIST).split(ChatRoom.GROUP_DIVIDER
            );*/

            /*
            * if (existingGroupMember[0].equal(XX)) {
            *
            * }
            *
            * */

            // TODO: this part need to be official
            List<User> userList = new ArrayList<>();
            User user = new User();
            user.setUser_id(1700003);
            user.setUsername("Mr X");
            userList.add(user);

            user = new User();
            user.setUser_id(1700004);
            user.setUsername("Mr X");
            userList.add(user);


            if (ChatFragment.CURRENT_USER_ID == 1700001) {
                user = new User();
                user.setUser_id(1700002);
                user.setUsername("Mr Test2 1700002");
                userList.add(user);
            } else {
                user = new User();
                user.setUser_id(1700001);
                user.setUsername("Mr Test1 1700001");
                userList.add(user);
            }


            return userList;
        }

        @Override
        protected void onPostExecute(List<User> userList) {
            super.onPostExecute(userList);

            try {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        sWeakReference.get(), LinearLayoutManager.VERTICAL, false
                );

                sWeakReference.get().mRecyclerView.setLayoutManager(linearLayoutManager);
                sWeakReference.get().mRecyclerView.setHasFixedSize(true);
                sWeakReference.get().mRecyclerView.setAdapter(new RecyclerViewAdapter(userList));

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }


    private static class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<User> mUserList;

        private RecyclerViewAdapter(List<User> userList) {
            mUserList = userList;
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mUserNameTextView;
            private ConstraintLayout mConstraintLayout;

            private ViewHolder(View itemView) {
                super(itemView);

                mUserNameTextView = itemView.findViewById(R.id.textView_itemUserSelection_name);
                mConstraintLayout = itemView.findViewById(R.id.layout);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                User user = mUserList.get(position);


                if (sWeakReference.get().mSelectionAction == SELECT_PRIVATE_CHAT_MEMBER) {

                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setStatus(ChatRoom.CHAT_ROOM_JOINED);
                    chatRoom.setChatRoomType(ChatRoom.PRIVATE_CHAT_ROOM);
                    chatRoom.setName(user.getUsername());
                    chatRoom.setDateTimeMessageReceived("");
                    chatRoom.setChatRoomUniqueTopic(user.getUser_id() + "");
                    chatRoom.setLatestMessage("");

                    new InsertAsyncTask(chatRoom).execute();

                } else {
                    if (setSelectItem(String.valueOf(user.getUser_id()), user.getUsername())) {
                        mConstraintLayout.setBackgroundColor(Color.WHITE);
                    } else {
                        mConstraintLayout.setBackgroundColor(Color.TRANSPARENT);
                    }
                }

            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ce_item_user_selection, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            User user = mUserList.get(position);

            holder.mUserNameTextView.setText(String.valueOf(user.getUser_id()));

            if (sWeakReference.get().mSelectedUserIdStringList.contains(String.valueOf(user.getUser_id()))) {
                holder.mConstraintLayout.setBackgroundColor(Color.WHITE);
            } else {
                holder.mConstraintLayout.setBackgroundColor(Color.TRANSPARENT);
            }

        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }

        private boolean setSelectItem(String userId, String username) {

            boolean objectExist;

            if (sWeakReference.get().mSelectedUserIdStringList.contains(userId)) {
                sWeakReference.get().mSelectedUserIdStringList.remove(userId);
                sWeakReference.get().mSelectedUserNameStringList.remove(username);
                objectExist = false;
            } else {
                sWeakReference.get().mSelectedUserIdStringList.add(userId);
                sWeakReference.get().mSelectedUserNameStringList.add(username);
                objectExist = true;
            }

            if (sWeakReference.get().mSelectedUserIdStringList.size() > 0) {
                sWeakReference.get().mMenuAddButton.setVisible(true);
            } else {
                sWeakReference.get().mMenuAddButton.setVisible(false);
            }


            return objectExist;
        }


    }

    private static class InsertAsyncTask extends AsyncTask<Void, Void, Void> {

        private ChatRoom mChatRoom;

        private InsertAsyncTask(ChatRoom chatRoom) {
            mChatRoom = chatRoom;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(sWeakReference.get());

            ChatRoom chatRoom = applicationDatabase.chatRoomDao()
                    .get(mChatRoom.getChatRoomUniqueTopic());

            if (chatRoom == null) {
                sChatRoomId = applicationDatabase.chatRoomDao().insert(mChatRoom);
                sChatRoomUniqueTopic = mChatRoom.getChatRoomUniqueTopic();
                sChatRoomName = mChatRoom.getName();
            } else {
                sChatRoomId = chatRoom.getId();
                sChatRoomUniqueTopic = chatRoom.getChatRoomUniqueTopic();
                sChatRoomName = chatRoom.getName();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sWeakReference.get().finish();
        }

    }


}
