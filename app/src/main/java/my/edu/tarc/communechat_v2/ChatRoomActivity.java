package my.edu.tarc.communechat_v2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Message;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class ChatRoomActivity extends AppCompatActivity {

    //READ ME before you start modifying this class
    //this chat engine was done using an amazing 3rd party library
    //source: https://github.com/timigod/android-chat-ui
    //this man has done most of the complex job
    //and we only need to setup the send and receive message process
    //and put in data into the ChatMessage class


    private SharedPreferences pref;
    private ChatView chatViewRoom;
    private ProgressBar progressBarChatRoom;
    private Chat_Room chatRoom;
    private MqttHelper chatMqttHelper;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (chatRoom.getRole().equals("Admin")) {
            getMenuInflater().inflate(R.menu.room_admin_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.room_member_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Intent intent;
        switch (itemId) {
            case R.id.nav_add_people:
                intent = new Intent(ChatRoomActivity.this, AddPeopleToChatActivity.class);
                intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom.getRoom_id());
                startActivity(intent);
                break;
            case R.id.nav_remove_people:
                intent = new Intent(ChatRoomActivity.this, RemovePeopleFromChatActivity.class);
                intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom.getRoom_id());
                startActivity(intent);
                break;
            case R.id.nav_exit_group:
                exitGroup();
                break;
            case R.id.nav_group_info:
                intent = new Intent(ChatRoomActivity.this, GroupInfoActivity.class);
                intent.putExtra(Chat_Room.COL_ROOM_ID, chatRoom.getRoom_id());
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatMqttHelper = new MqttHelper();
        chatMqttHelper.connect(this);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init views
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        chatViewRoom = findViewById(R.id.chatView_room);
        progressBarChatRoom = findViewById(R.id.progressBar_chatRoom);

        chatRoom = new Chat_Room();
        chatRoom.setRole(getIntent().getStringExtra(Participant.COL_ROLE));
        chatRoom.setRoom_id(getIntent().getIntExtra(Chat_Room.COL_ROOM_ID, -1));
        chatMqttHelper.subscribe("sendMessage/room" + chatRoom.getRoom_id());
        chatMqttHelper.getMqttClient().setCallback(chatRoomCallback);
        chatRoom.setSecret_key(pref.getString(RoomSecretHelper.getRoomPrefKey(chatRoom.getRoom_id()),null).getBytes());

        if (hasRoomID()) {
            initializeChatRoomByRoomID();
        } else {
            //todo init room by user id and target user id, new chat room
        }

        chatViewRoom.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                Calendar calendar = Calendar.getInstance();
                String topic = "sendMessage/room" + chatRoom.getRoom_id();
                String header = MqttHeader.SEND_ROOM_MESSAGE;
                Message message = new Message();
                message.setSender_id(pref.getInt(User.COL_USER_ID, -1));
                message.setDate_created(calendar);
                //message.setMessage(chatRoom.encryptMessage(chatViewRoom.getTypedMessage()));
                message.setMessage(chatViewRoom.getTypedMessage());
                message.setRoom_id(chatRoom.getRoom_id());
                message.setMessage_type("Text");
                message.setSender_name(pref.getString(User.COL_DISPLAY_NAME, ""));

                chatViewRoom.addMessage(new ChatMessage(
                        chatViewRoom.getTypedMessage(),
                        calendar.getTimeInMillis(),
                        ChatMessage.Type.SENT,
                        getString(R.string.you)
                ));
                chatMqttHelper.publish(topic, header, message);
                chatViewRoom.getInputEditText().setText("");

                //make sure to return false
                //return true the chat view will update automatically
                return false;
            }
        });
    }

    private MqttCallback chatRoomCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            chatMqttHelper.decode(message.toString());
            try {
                JSONObject result = new JSONObject(chatMqttHelper.getReceivedResult());

                if (result.getInt(Message.COL_ROOM_ID) != chatRoom.getRoom_id()) {
                    return;
                }

                if (result.getInt(Message.COL_SENDER_ID) == pref.getInt(User.COL_USER_ID, -1)) {
                    return;
                }

                Message received_message = new Message();
                received_message.setSender_id(result.getInt(Message.COL_SENDER_ID));
                //received_message.setMessage(chatRoom.decryptMessage(result.getString(Message.COL_MESSAGE)));
                received_message.setMessage(result.getString(Message.COL_MESSAGE));
                received_message.setDate_created(result.getString(Message.COL_DATE_CREATED));
                received_message.setMessage_type(result.getString(Message.COL_MESSAGE_TYPE));
                received_message.setRoom_id(result.getInt(Message.COL_ROOM_ID));
                received_message.setSender_name(result.getString(Message.COL_SENDER_NAME));

                chatViewRoom.addMessage(new ChatMessage(
                        received_message.getMessage(),
                        received_message.getDate_created().getTimeInMillis(),
                        ChatMessage.Type.RECEIVED,
                        received_message.getSender_name()
                ));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private void initializeChatRoomByRoomID() {
        //show progress bar
        progressBarChatRoom.setVisibility(View.VISIBLE);

        final Chat_Room chatRoom = new Chat_Room();
        chatRoom.setRoom_id(getIntent().getIntExtra(Chat_Room.COL_ROOM_ID, -1));
        chatRoom.setRoom_name(getIntent().getStringExtra(Chat_Room.COL_ROOM_NAME));
        chatRoom.setSecret_key(pref.getString(RoomSecretHelper.getRoomPrefKey(chatRoom.getRoom_id()),null).getBytes());
        setTitle(chatRoom.getRoom_name());

        String topic = "getRoomMessage/room" + chatRoom.getRoom_id() + "_user" + pref.getInt(User.COL_USER_ID, -1);
        String header = MqttHeader.GET_ROOM_MESSAGE;
        mqttHelper.connectPublishSubscribe(this, topic, header, chatRoom);
        mqttHelper.getMqttClient().setCallback(getMessageCallback);
    }

    private MqttCallback getMessageCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
            mqttHelper.decode(message.toString());
            if (mqttHelper.getReceivedHeader().equals(MqttHeader.GET_ROOM_MESSAGE_REPLY)) {
                if (!mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                    try {
                        JSONArray result = new JSONArray(mqttHelper.getReceivedResult());
                        ArrayList<ChatMessage> messages = new ArrayList<>();

                        for (int i = 0; i <= result.length() - 1; i++) {
                            JSONObject temp = result.getJSONObject(i);
                            Message room_message = new Message();
                            room_message.setDate_created(temp.getString(Message.COL_DATE_CREATED));
                            room_message.setSender_id(temp.getInt(Message.COL_SENDER_ID));

                            ChatMessage chatMessage = new ChatMessage(
                                    //chatRoom.decryptMessage(temp.getString(Message.COL_MESSAGE)), //message content
                                    temp.getString(Message.COL_MESSAGE), //message content
                                    room_message.getDate_created().getTimeInMillis(), //date
                                    pref.getInt(User.COL_USER_ID, -1) == room_message.getSender_id()
                                            ? ChatMessage.Type.SENT //if user id in pref == sender id, then is sender
                                            : ChatMessage.Type.RECEIVED, //else is receiver
                                    pref.getString(User.COL_DISPLAY_NAME, "").equals(temp.getString(User.COL_DISPLAY_NAME))
                                            ? getString(R.string.you)
                                            : temp.getString(User.COL_DISPLAY_NAME)//sender name
                            );

                            messages.add(chatMessage);
                        }
                        chatViewRoom.addMessages(messages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            progressBarChatRoom.setVisibility(View.GONE);
            mqttHelper.unsubscribe(topic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private boolean hasRoomID() {
        return getIntent().getIntExtra(Chat_Room.COL_ROOM_ID, -1) != -1;
    }

    private void exitGroup() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.exit_group));
        alertDialog.setMessage(R.string.exit_group_desc);
        alertDialog.setNegativeButton(getString(R.string.cancel), null);
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmedExitGroup();
            }
        });
        alertDialog.show();
    }

    private void confirmedExitGroup() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatRoomActivity.this);

        String topic = "exitGroup/" + pref.getInt(User.COL_USER_ID, -1);
        String header = MqttHeader.DELETE_CHAT_ROOM;
        Participant participant = new Participant();
        participant.setRoom_id(chatRoom.getRoom_id());
        participant.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        mqttHelper.connectPublishSubscribe(this, topic, header, participant);
        mqttHelper.getMqttClient().setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                mqttHelper.decode(message.toString());
                if (mqttHelper.getReceivedHeader().equals(MqttHeader.DELETE_CHAT_ROOM_REPLY)) {
                    if (mqttHelper.getReceivedResult().equals(MqttHeader.SUCCESS)) {
                        alertDialog.setTitle(getString(R.string.success));
                        alertDialog.setMessage(R.string.exit_group_success_desc);
                        alertDialog.setNeutralButton(getString(R.string.ok), null);
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatMqttHelper.disconnect();
    }
}
