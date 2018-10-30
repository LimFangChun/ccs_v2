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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.chatEngine.database.ApplicationDatabase;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;
import my.edu.tarc.communechat_v2.model.User;

public class AddGroupActivity extends AppCompatActivity {

    private EditText mGroupNameEditText;
    public static final String GROUP_NAME = "GroupName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check whether action bar is initialize
        if (getSupportActionBar() != null) {
            // Set return back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mGroupNameEditText = findViewById(R.id.editText_addGroupActivity_name);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ce_menu_contact_selection, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.ce_menu_contact_selection_add) {

            /*ChatRoom chatRoom = new ChatRoom();
            chatRoom.setStatus(ChatRoom.CHAT_ROOM_JOINED);
            chatRoom.setChatRoomType(ChatRoom.GROUP_CHAT_ROOM);
            chatRoom.setName(mGroupNameEditText.getText().toString());
            chatRoom.setDateTimeMessageReceived("");
            chatRoom.setChatRoomUniqueTopic(ChatFragment.CURRENT_USER_ID + "_" + (new MyDateTime().getDateTime()));
            chatRoom.setLatestMessage("");*/

            //new InsertAsyncTask(this, chatRoom).execute();
            Intent intent = new Intent();
            intent.putExtra(GROUP_NAME, mGroupNameEditText.getText().toString());
            setResult(RESULT_OK, intent);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private static class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private static WeakReference<SelectContactActivity> mWeakReference;
        private List<User> mUserList;
        private RecyclerViewAdapter(List<User> userList, SelectContactActivity selectContactActivity) {
            mUserList = userList;
            mWeakReference = new WeakReference<>(selectContactActivity);
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView mIconImageView;
            private TextView mUserNameTextView;
            private ConstraintLayout mConstraintLayout;

            private ViewHolder(View itemView) {
                super(itemView);

                mUserNameTextView = itemView.findViewById(R.id.textView_itemUserSelection_name);
                mConstraintLayout = itemView.findViewById(R.id.layout);

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
            holder.mUserNameTextView.setText(user.getUser_id());

        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }



    }


    private static class StartupAsyncTask extends AsyncTask<Void, Void, List<User>> {

        private WeakReference<SelectContactActivity> mWeakReference;

        private StartupAsyncTask(SelectContactActivity selectContactActivity) {
            mWeakReference = new WeakReference<>(selectContactActivity);
        }

        @Override
        protected List<User> doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(mWeakReference.get());

            // TODO: this part need to be official
            List<User> userList = new ArrayList<>();
            User user = new User();
            user.setUser_id(1709498);
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

            // FIXME: this place got bug every time start instant run
            try {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        mWeakReference.get(), LinearLayoutManager.VERTICAL, false
                );

                /*mWeakReference.get().mRecyclerView.setLayoutManager(linearLayoutManager);
                mWeakReference.get().mRecyclerView.setHasFixedSize(true);

                mWeakReference.get().mRecyclerView.setAdapter(
                        new RecyclerViewAdapter(userList, mWeakReference.get()));*/


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

}
