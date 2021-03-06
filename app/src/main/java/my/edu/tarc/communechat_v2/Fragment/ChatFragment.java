package my.edu.tarc.communechat_v2.Fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import my.edu.tarc.communechat_v2.Adapter.ChatListAdapter;
import my.edu.tarc.communechat_v2.AddGroupChatActivity;
import my.edu.tarc.communechat_v2.Background.BackgroundService;
import my.edu.tarc.communechat_v2.ChatBotActivity;
import my.edu.tarc.communechat_v2.ChatRoomActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.User;

import static android.app.Activity.RESULT_OK;
import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class ChatFragment extends Fragment {

    public static final int REQUEST_CHAT_ROOM = 10;

    private SharedPreferences pref;
    private ListView listViewChatList;
    private ProgressBar progressBarChat;
    private TextView textViewNoHistory;
    private Bundle savedInstanceState;
    private FloatingActionButton fabAddGroup;

    @Override
    public void onResume() {
        super.onResume();
        initializeChatRoom();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //init views
        listViewChatList = view.findViewById(R.id.listView_chatList);
        progressBarChat = view.findViewById(R.id.progressBar_chatList);
        progressBarChat.setVisibility(View.VISIBLE);
        progressBarChat.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        textViewNoHistory = view.findViewById(R.id.textView_chatFragment_Description);
        fabAddGroup = view.findViewById(R.id.fab_add);
        fabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).startActivityForResult(new Intent(getActivity(), AddGroupChatActivity.class), REQUEST_CHAT_ROOM);
            }
        });

        initializeListViewListener();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CHAT_ROOM) {
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra(Chat_Room.COL_ROOM_ID, data.getIntExtra(Chat_Room.COL_ROOM_ID, -1));
                intent.putExtra(Chat_Room.COL_ROOM_TYPE, data.getStringExtra(Chat_Room.COL_ROOM_TYPE));
                intent.putExtra(Participant.COL_ROLE, data.getStringExtra(Participant.COL_ROLE));
                startActivity(intent);
            }
        }
    }

    private void initializeListViewListener() {
        listViewChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textViewRoomID = view.findViewById(R.id.textView_roomID);
                TextView textViewRoomName = view.findViewById(R.id.textView_header);
                TextView textViewRole = view.findViewById(R.id.textView_role);
                TextView textViewRoomType = view.findViewById(R.id.textView_roomType);

                Chat_Room chatRoom = new Chat_Room();
                chatRoom.setRoom_id(Integer.parseInt(textViewRoomID.getText().toString()));
                chatRoom.setRoom_name(textViewRoomName.getText().toString());
                chatRoom.setRole(textViewRole.getText().toString());
                chatRoom.setRoom_type(textViewRoomType.getText().toString());

                if (chatRoom.getRoom_id() == 0) {
                    Intent intent = new Intent(getActivity(), ChatBotActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                    intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom.getRoom_id());
                    intent.putExtra(Chat_Room.COL_ROOM_NAME, chatRoom.getRoom_name());
                    intent.putExtra(Participant.COL_ROLE, chatRoom.getRole());
                    intent.putExtra(Chat_Room.COL_ROOM_TYPE, chatRoom.getRoom_type());
                    startActivity(intent);
                }
            }
        });
    }

    private void initializeChatRoom() {
        //init chat room
        User user = new User();
        user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        if (user.getUser_id() == -1) {
            Toast.makeText(getContext(), "Couldn't init chat room", Toast.LENGTH_LONG).show();
            return;
        }
        mqttHelper.connectPublishSubscribe(getActivity(),
                "getChatRoom/" + user.getUser_id(),
                MqttHeader.GET_CHAT_ROOM,
                user);
        mqttHelper.getMqttClient().setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                MqttHelper helper = new MqttHelper();
                helper.decode(message.toString());
                if (helper.getReceivedHeader().equals(MqttHeader.GET_CHAT_ROOM_REPLY)) {
                    if (helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                        //no result
                        textViewNoHistory.setVisibility(View.VISIBLE);
                    } else {
                        //received json array result
                        //process json array
                        try {
                            JSONArray result = new JSONArray(helper.getReceivedResult());
                            int[] roomID = new int[result.length()];

                            ArrayList<Chat_Room> resultList = new ArrayList<>();
                            //add first item that redirect to chat bot activity
                            Chat_Room chatBot = new Chat_Room();
                            chatBot.setRoom_id(0);
                            chatBot.setRoom_name("Chat bot (FAQ)");
                            chatBot.setOwner_id(0);
                            chatBot.setRole("ChatBot");
                            chatBot.setRoom_type("ChatBot");
                            resultList.add(chatBot);

                            //add rest of the item to chat list
                            for (int i = 0; i <= result.length() - 1; i++) {
                                JSONObject temp = result.getJSONObject(i);
                                Chat_Room room = new Chat_Room();
                                room.setRoom_id(temp.getInt(Chat_Room.COL_ROOM_ID));
                                room.setRoom_name(temp.getString(Chat_Room.COL_ROOM_NAME));
                                room.setOwner_id(temp.getInt(Chat_Room.COL_OWNER_ID));
                                room.setLast_update(temp.getString(Chat_Room.COL_LAST_UPDATE));
                                room.setRole(temp.getString(Participant.COL_ROLE));
                                room.setRoom_type(temp.getString(Chat_Room.COL_ROOM_TYPE));

                                resultList.add(room);
                                roomID[i] = room.getRoom_id();
                            }
                            ChatListAdapter adapter = new ChatListAdapter(getContext(), R.layout.adapter_chat_list, resultList);
                            listViewChatList.setAdapter(adapter);

                            if (savedInstanceState == null) {
                                runBackgroundService(roomID);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                progressBarChat.setVisibility(View.GONE);
                mqttHelper.unsubscribe(topic);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void runBackgroundService(int[] roomID) {
        if (!isMyServiceRunning(BackgroundService.class) && getActivity() != null) {
            Log.d("ChatFragment", "starting background service");
            pref.edit().putString(Chat_Room.COL_ROOM_ID, Arrays.toString(roomID)).apply();
            Intent intent = new Intent(getActivity(), BackgroundService.class);
            Objects.requireNonNull(getContext()).startService(intent);
        }
    }

    private void sleep(double second) {
        try {
            int time = (int) (second * 1000);
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        if (getActivity() != null) {
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            assert manager != null;
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }

        return false;
    }
}