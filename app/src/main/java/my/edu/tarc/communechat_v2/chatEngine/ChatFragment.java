package my.edu.tarc.communechat_v2.chatEngine;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.chatEngine.database.ApplicationDatabase;
import my.edu.tarc.communechat_v2.chatEngine.database.Chat;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;
import my.edu.tarc.communechat_v2.chatEngine.testData.TestData;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TextView mDescriptionTextView;
    public static final String CHAT_ENGINE_SHARE_PREFERENCES = "ChatEngineSharePreferences";
    public static final String CHAT_ENGINE_MESSAGE_RECEIVED = "ChatEngineMessageReceived";
    public static final String CHAT_ENGINE_USER_ID = "ChatEngineUserId";
    public static final String TAG = "ChatFragment";
    private static WeakReference<ChatFragment> mWeakReference;

    public static final String SELECTED_CHAT_ROOM_ID = "SelectedChatRoomId";
    public static final String SELECTED_CHAT_ROOM_UNIQUE_TOPIC = "SelectedChatRoomUniqueTopic";


    public static final int CURRENT_USER_ID = 1700001;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerView_chatFragment);

        mDescriptionTextView = view.findViewById(R.id.textView_chatFragment_Description);

        mWeakReference = new WeakReference<>(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //MainActivity.mqttHelper.subscribe(ChatFragment.CURRENT_USER_ID +"");

        if (SelectContactActivity.sChatRoomId != -1) {
            Intent intent = new Intent(getContext(), ChatRoomActivity.class);
            intent.putExtra(SELECTED_CHAT_ROOM_ID, SelectContactActivity.sChatRoomId);
            intent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, SelectContactActivity.sChatRoomUniqueTopic);
            startActivity(intent);
        }
        // Inform Which page is the user currently at
        //TODO:
        getActivity().getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
                .edit().putString(ChatFragment.CHAT_ENGINE_MESSAGE_RECEIVED, TAG).apply();
        getActivity().getSharedPreferences(ChatFragment.CHAT_ENGINE_SHARE_PREFERENCES, MODE_PRIVATE)
                .edit().putInt(ChatFragment.CHAT_ENGINE_USER_ID, CURRENT_USER_ID).apply();

        new StartupAsyncTask(this).execute();
    }

    public static void refreshPage(){
        Log.i(TAG, "Refreshing Page");
        mWeakReference.get().onResume();
    }

    private static class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private static WeakReference<ChatFragment> mWeakReference;
        private ActionCallBackAdapter mActionCallBackAdapter;
        private List<ChatRoom> mChatRoomList;

        private RecyclerViewAdapter(List<ChatRoom> chatRoomList, ChatFragment chatFragment) {
            mChatRoomList = chatRoomList;
            mWeakReference = new WeakReference<>(chatFragment);
            mActionCallBackAdapter = new ActionCallBackAdapter(
                    mWeakReference.get(), this, mWeakReference.get().mRecyclerView
            );
        }

        protected class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {

            private ImageView mIconImageView;
            private TextView mNameTextView, mMessageTextView, mDateTimeTextView;
            private ConstraintLayout mConstraintLayout;

            private ViewHolder(View itemView) {
                super(itemView);

                mIconImageView = itemView.findViewById(R.id.imageView_itemChatList_icon);
                mNameTextView = itemView.findViewById(R.id.textView_itemChatList_name);
                mMessageTextView = itemView.findViewById(R.id.textView_itemChatList_message);
                mDateTimeTextView = itemView.findViewById(R.id.textView_itemChatList_time);

                mConstraintLayout = itemView.findViewById(R.id.layout);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {


                if (mActionCallBackAdapter.checkActionModeCallBackOpen()) {

                    if (mActionCallBackAdapter.setSelectItem(
                            mChatRoomList.get(getAdapterPosition()), getAdapterPosition())
                            ) {
                        mConstraintLayout.setBackgroundColor(Color.WHITE);
                    } else {
                        mConstraintLayout.setBackgroundColor(Color.TRANSPARENT);
                    }
                } else {
                    Intent intent = new Intent(mWeakReference.get().getContext(), ChatRoomActivity.class);
                    // Inform which chat room is selected
                    intent.putExtra(SELECTED_CHAT_ROOM_ID, mChatRoomList.get(getAdapterPosition()).getId());
                    intent.putExtra(SELECTED_CHAT_ROOM_UNIQUE_TOPIC, mChatRoomList.get(getAdapterPosition()).getChatRoomUniqueTopic());
                    mWeakReference.get().startActivity(intent);
                }

            }

            @Override
            public boolean onLongClick(View view) {

                int position = getAdapterPosition();

                if (!mActionCallBackAdapter.checkActionModeCallBackOpen()) {
                    mWeakReference.get().getActivity().startActionMode(
                            mActionCallBackAdapter
                    );
                    mActionCallBackAdapter.openActionModeCallBack();
                }


                ChatRoom chatRoom = mChatRoomList.get(position);

                if (mActionCallBackAdapter.setSelectItem(chatRoom, position)) {
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
                    .inflate(R.layout.ce_item_chat_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            ChatRoom chatRoom = mChatRoomList.get(position);

            holder.mNameTextView.setText(chatRoom.getName());
            holder.mMessageTextView.setText(chatRoom.getLatestMessage());
            holder.mDateTimeTextView.setText(new MyDateTime().getTimeFormatSetting(chatRoom.getDateTimeMessageReceived()));

            if (mActionCallBackAdapter.mSelectedChatRoomList.contains(chatRoom)) {
                holder.mConstraintLayout.setBackgroundColor(Color.WHITE);
            } else {
                holder.mConstraintLayout.setBackgroundColor(Color.TRANSPARENT);
            }

            // TODO: Later Implement
            //holder.mIconImageView.setImageBitmap();

        }

        @Override
        public int getItemCount() {
            return mChatRoomList.size();
        }

    }

    private static class StartupAsyncTask extends AsyncTask<Void,Void,List<ChatRoom>> {

        private WeakReference<ChatFragment> mWeakReference;

        private StartupAsyncTask(ChatFragment chatFragment) {
            mWeakReference = new WeakReference<>(chatFragment);
        }

        @Override
        protected List<ChatRoom> doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase
                    = ApplicationDatabase.build(mWeakReference.get().getContext());

            return applicationDatabase.chatRoomDao().getAllChatRoom(String.valueOf(ChatFragment.CURRENT_USER_ID));
        }

        @Override
        protected void onPostExecute(List<ChatRoom> chatRoomList) {
            super.onPostExecute(chatRoomList);

            if (chatRoomList.size() == 0) {
                mWeakReference.get().mDescriptionTextView.setVisibility(View.VISIBLE);
            } else {
                mWeakReference.get().mDescriptionTextView.setVisibility(View.GONE);
            }

            // FIXME: this place got bug every time start instant run
            try {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        mWeakReference.get().getContext(),
                        LinearLayoutManager.VERTICAL, false
                );

                mWeakReference.get().mRecyclerView.setLayoutManager(linearLayoutManager);
                mWeakReference.get().mRecyclerView.setHasFixedSize(true);


                mWeakReference.get().mRecyclerView.setItemViewCacheSize(20);
                mWeakReference.get().mRecyclerView.setDrawingCacheEnabled(true);
                mWeakReference.get().mRecyclerView
                        .setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                mWeakReference.get().mRecyclerView.addItemDecoration(new DividerItemDecoration(mWeakReference.get().getContext(), DividerItemDecoration.VERTICAL));

                mWeakReference.get().mRecyclerView.setAdapter(
                        new RecyclerViewAdapter(chatRoomList, mWeakReference.get())
                );

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

    private static class ActionCallBackAdapter implements ActionMode.Callback {

        private WeakReference<ChatFragment> mWeakReference;
        private boolean mIsActionModeCallBackOpen = false;
        private boolean mIsSelectedChatRoomDeleted = false;
        private ActionMode mActionMode;
        private RecyclerViewAdapter mRecyclerViewAdapter;
        private RecyclerView mRecyclerView;
        private List<Integer> mPositionIntegerList = new ArrayList<>();
        private List<ChatRoom> mSelectedChatRoomList = new ArrayList<>();

        private ActionCallBackAdapter(ChatFragment chatFragment,
                                      RecyclerViewAdapter recyclerViewAdapter,
                                      RecyclerView recyclerView) {
            mWeakReference = new WeakReference<>(chatFragment);
            mRecyclerViewAdapter = recyclerViewAdapter;
            mRecyclerView = recyclerView;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            mWeakReference.get().getActivity().getMenuInflater()
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

            mIsSelectedChatRoomDeleted = true;
            actionMode.finish();
            new DeleteAsyncTask(mSelectedChatRoomList, actionMode,mWeakReference.get()).execute();
            return true;

        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

            if (mIsSelectedChatRoomDeleted) {
                for (int i = 0; i < mSelectedChatRoomList.size(); i++) {
                    mRecyclerViewAdapter.mChatRoomList.remove(mSelectedChatRoomList.get(i));
                    mRecyclerView.removeViewAt(mPositionIntegerList.get(i));
                    mRecyclerViewAdapter.notifyItemRemoved(mPositionIntegerList.get(i));
                }

            } else {
                mRecyclerViewAdapter.notifyDataSetChanged();
            }

            mSelectedChatRoomList.clear();
            mPositionIntegerList.clear();
            mIsSelectedChatRoomDeleted = false;
            mIsActionModeCallBackOpen = false;


            //actionMode.finish();
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
        public boolean setSelectItem(ChatRoom chatRoom, int itemPosition) {
            boolean returnValue;

            if (mSelectedChatRoomList.contains(chatRoom)) {
                mPositionIntegerList.remove(Integer.valueOf(itemPosition));
                mSelectedChatRoomList.remove(chatRoom);
                if (mSelectedChatRoomList.isEmpty()) {
                    onDestroyActionMode(mActionMode);
                    mActionMode.finish();
                } else {
                    mActionMode.setTitle(String.valueOf(mSelectedChatRoomList.size()));
                }
                returnValue = false;

            } else {
                mPositionIntegerList.add(itemPosition);
                mSelectedChatRoomList.add(chatRoom);
                mActionMode.setTitle(String.valueOf(mSelectedChatRoomList.size()));
                returnValue = true;
            }

            return returnValue;
        }

    }

    private static class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<ChatFragment> mWeakReference;
        private ActionMode mActionMode;
        private List<ChatRoom> mChatRoomList;

        private DeleteAsyncTask(List<ChatRoom> chatRoomList, ActionMode actionMode,
                                       ChatFragment chatFragment) {
            mWeakReference = new WeakReference<>(chatFragment);
            mActionMode = actionMode;
            mChatRoomList = chatRoomList;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ApplicationDatabase applicationDatabase =
                    ApplicationDatabase.build(mWeakReference.get().getContext());

            // Remove chat room
            applicationDatabase.chatRoomDao().deleteManyChatRoom(mChatRoomList);

            List<Long> roomId = new ArrayList<>();
            for (int i = 0; i < mChatRoomList.size(); i++) {
                roomId.add(mChatRoomList.get(i).getId());
            }
            // Remove all the chat that is store in the chat history
            applicationDatabase.chatDao().deleteAllChatInChatRoom(roomId);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
