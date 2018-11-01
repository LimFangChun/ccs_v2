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
    public static final String GROUP_NAME = "AddGroupNameActivity";
    private RecyclerView mRecyclerView;
    private static WeakReference<AddGroupActivity> sWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sWeakReference = new WeakReference<>(this);

        // Check whether action bar is initialize
        if (getSupportActionBar() != null) {
            // Set return back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = findViewById(R.id.recyclerView_addGroup);
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

            Intent intent = new Intent();
            intent.putExtra(GROUP_NAME, mGroupNameEditText.getText().toString());
            setResult(RESULT_OK, intent);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new StartupAsyncTask().execute();
    }

    private static class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<User> mUserList;
        private RecyclerViewAdapter(List<User> userList) {
            mUserList = userList;
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mUserNameTextView;

            private ViewHolder(View itemView) {
                super(itemView);

                mUserNameTextView = itemView.findViewById(R.id.textView_itemUserSelection_name);

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
            holder.mUserNameTextView.setText("#" + user.getUser_id() + "\n@" + user.getUsername());

        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }



    }


    private static class StartupAsyncTask extends AsyncTask<Void, Void, List<User>> {


        private StartupAsyncTask() {
        }

        @Override
        protected List<User> doInBackground(Void... voids) {

            String[] userDetail = sWeakReference.get().getIntent().getStringExtra(SelectContactActivity.SELECTED_GROUP_MEMBER_USER_ID).split(ChatRoom.GROUP_DIVIDER);

            // TODO: this part need to be official
            List<User> userList = new ArrayList<>();
            User user;

            for (String userInfo : userDetail) {
                String[] userIdName = userInfo.split(ChatRoom.ID_NAME_DIVIDER);
                user = new User();
                user.setUsername(userIdName[1]);
                user.setUser_id(Integer.parseInt(userIdName[0]));
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

                sWeakReference.get().mRecyclerView.setAdapter(
                        new RecyclerViewAdapter(userList));


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

}
