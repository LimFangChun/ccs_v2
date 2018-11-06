package my.edu.tarc.communechat_v2.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

import my.edu.tarc.communechat_v2.Adapter.ChatListAdapter;
import my.edu.tarc.communechat_v2.ChatRoomActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class ChatFragment extends Fragment {

    private SharedPreferences pref;
    private ListView listViewChatList;
    private ProgressBar progressBarChat;
    private TextView textViewNoHistory;

    @Override
    public void onResume() {
        super.onResume();
        initializeChatRoom();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //init views
        listViewChatList = view.findViewById(R.id.listView_chatList);
        progressBarChat = view.findViewById(R.id.progressBar_chatList);
        progressBarChat.setVisibility(View.VISIBLE);
        progressBarChat.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        textViewNoHistory = view.findViewById(R.id.textView_chatFragment_Description);

        initializeListViewListener();
        return view;
    }

    private void initializeListViewListener() {
        listViewChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textViewRoomID = view.findViewById(R.id.textView_roomID);
                TextView textViewRoomName = view.findViewById(R.id.textView_header);
                TextView textViewRole = view.findViewById(R.id.textView_role);

                Chat_Room chatRoom = new Chat_Room();
                chatRoom.setRoom_id(Integer.parseInt(textViewRoomID.getText().toString()));
                chatRoom.setRoom_name(textViewRoomName.getText().toString());
                chatRoom.setRole(textViewRole.getText().toString());

                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom.getRoom_id());
                intent.putExtra(Chat_Room.COL_ROOM_NAME, chatRoom.getRoom_name());
                intent.putExtra(Participant.COL_ROLE, chatRoom.getRole());
                startActivity(intent);
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
                Log.d("ChatFragment", message.toString());
                mqttHelper.decode(message.toString());
                if (mqttHelper.getReceivedHeader().equals(MqttHeader.GET_CHAT_ROOM_REPLY)) {
                    if (mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                        //no result
                        textViewNoHistory.setVisibility(View.VISIBLE);
                    } else {
                        //received json array result
                        //process json array
                        try {
                            JSONArray result = new JSONArray(mqttHelper.getReceivedResult());

                            ArrayList<Chat_Room> resultList = new ArrayList<>();
                            for (int i = 0; i <= result.length() - 1; i++) {
                                JSONObject temp = result.getJSONObject(i);
                                Chat_Room room = new Chat_Room();
                                room.setRoom_id(temp.getInt(Chat_Room.COL_ROOM_ID));
                                room.setRoom_name(temp.getString(Chat_Room.COL_ROOM_NAME));
                                room.setOwner_id(temp.getInt(Chat_Room.COL_OWNER_ID));
                                room.setLast_update(temp.getString(Chat_Room.COL_LAST_UPDATE));
                                room.setRole(temp.getString(Participant.COL_ROLE));

                                resultList.add(room);
                            }
                            ChatListAdapter adapter = new ChatListAdapter(getContext(), R.layout.adapter_chat_list, resultList);
                            listViewChatList.setAdapter(adapter);
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

    private void sleep(double second) {
        try {
            int time = (int) (second * 1000);
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
