package my.edu.tarc.communechat_v2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import my.edu.tarc.communechat_v2.Utility.myUtil;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Message;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class ChatRoomActivity extends AppCompatActivity {
    private static final String TAG = "ChatRoomActivity";

    private SharedPreferences pref;
    private ProgressBar progressBarChatRoom;
    private Chat_Room chatRoom;
    private MqttHelper chatMqttHelper;
    private String topic;

    private com.shrikanthravi.chatview.widget.ChatView chatView;
    public static final int REQUEST_CAMERA = 0;
    public static final int REQUEST_GALLERY = 1;

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

        chatView();

        chatMqttHelper = new MqttHelper();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init views
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        progressBarChatRoom = findViewById(R.id.progressBar_chatRoom);

        chatRoom = new Chat_Room();
        chatRoom.setRole(getIntent().getStringExtra(Participant.COL_ROLE));
        chatRoom.setRoom_id(getIntent().getIntExtra(Chat_Room.COL_ROOM_ID, -1));

        String secretKey = pref.getString(RoomSecretHelper.getRoomPrefKey(chatRoom.getRoom_id()), null);
        if (secretKey == null) {
            //chatViewRoom.getInputEditText().setEnabled(false);
            //chatViewRoom.getInputEditText().setHint("Initializing... please try again later.");
            //Todo: request secret key for this chat room
        } else {
            chatRoom.setSecret_key(secretKey);
        }
        topic = MqttHeader.SEND_ROOM_MESSAGE + "/room" + chatRoom.getRoom_id();

        chatMqttHelper.connectSubscribe(this, topic);
        chatMqttHelper.getMqttClient().setCallback(chatRoomCallback);
        //chatRoom.setSecret_key(pref.getString(RoomSecretHelper.getRoomPrefKey(chatRoom.getRoom_id()),null).getBytes());

        if (isNetworkAvailable()) {
            initializeChatRoomByRoomID();
        } else {
            //initializeLocalChatRoom();
        }

//        chatViewRoom.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
//            @Override
//            public boolean sendMessage(ChatMessage chatMessage) {
//                if (chatViewRoom.getTypedMessage().isEmpty()) {
//                    return false;
//                }
//
//                Calendar calendar = Calendar.getInstance();
//                String header = MqttHeader.SEND_ROOM_MESSAGE;
//                //String topic = header + "/room" + chatRoom.getRoom_id();
//                Message message = new Message();
//                message.setSender_id(pref.getInt(User.COL_USER_ID, -1));
//                message.setDate_created(calendar);
//                //message.setMessage(chatRoom.encryptMessage(chatViewRoom.getTypedMessage()));
//                message.setMessage(chatViewRoom.getTypedMessage());
//                message.setRoom_id(chatRoom.getRoom_id());
//                message.setMessage_type("Text");
//                message.setSender_name(pref.getString(User.COL_DISPLAY_NAME, ""));
//
//                chatViewRoom.addMessage(new ChatMessage(
//                        chatViewRoom.getTypedMessage(),
//                        calendar.getTimeInMillis(),
//                        ChatMessage.Type.SENT,
//                        ""
//                ));
//                chatMqttHelper.publish(topic, header, message);
//                chatViewRoom.getInputEditText().setText("");
//
//                //make sure to return false
//                //return true the chat view will update automatically
//                return false;
//            }
//        });
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
                received_message.setMedia(result.getString(Message.COL_MEDIA).getBytes());

//                chatViewRoom.addMessage(new ChatMessage(
//                        received_message.getMessage(),
//                        received_message.getDate_created().getTimeInMillis(),
//                        ChatMessage.Type.RECEIVED,
//                        received_message.getSender_name()
//                ));

                //make a short vibration or sound
                //depend on user's mode
                makeVibrationOrSound();


                Log.d("CCC0", "Entered Result");
                addMessage(pref.getInt(User.COL_USER_ID, -1) == received_message.getSender_id(), received_message.getSender_name(), received_message.getDate_created().getTime().toString(), received_message.getMessage_type(), received_message.getMessage(), received_message.getMedia());


                //chatView.addMessage();

                //don't unsubscribe from the topic
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

        if (!"".equals(chatRoom.getRoom_name())) {
            setTitle(chatRoom.getRoom_name());
        }

        //chatRoom.setSecret_key(pref.getString(RoomSecretHelper.getRoomPrefKey(chatRoom.getRoom_id()),null).getBytes());

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
            MqttHelper helper = new MqttHelper();
            helper.decode(message.toString());
            if (helper.getReceivedHeader().equals(MqttHeader.GET_ROOM_MESSAGE_REPLY)) {
                if (!helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                    try {
                        JSONArray result = new JSONArray(helper.getReceivedResult());
                        //ArrayList<ChatMessage> messages = new ArrayList<>();

                        for (int i = 0; i <= result.length() - 1; i++) {
                            JSONObject temp = result.getJSONObject(i);
                            Message room_message = new Message();
                            room_message.setDate_created(temp.getString(Message.COL_DATE_CREATED));
                            room_message.setSender_id(temp.getInt(Message.COL_SENDER_ID));

                            //ChatMessage chatMessage = new ChatMessage(
                                    //chatRoom.decryptMessage(temp.getString(Message.COL_MESSAGE)), //message content
                            temp.getString(Message.COL_MESSAGE); //message content
//                                    room_message.getDate_created().getTimeInMillis(), //date
//                                    pref.getInt(User.COL_USER_ID, -1) == room_message.getSender_id()
//                                            ? ChatMessage.Type.SENT //if user id in pref == sender id, then is sender
//                                            : ChatMessage.Type.RECEIVED, //else is receiver
//                                    pref.getString(User.COL_DISPLAY_NAME, "").equals(temp.getString(User.COL_DISPLAY_NAME))
//                                            ? ""
//                                            : temp.getString(User.COL_DISPLAY_NAME)//sender name
//                            );
//                            messages.add(chatMessage);

                            if (!temp.getString(Message.COL_MESSAGE_TYPE).equals("Text")) {
                                byte[] z2 = Base64.decode(temp.getString(Message.COL_MEDIA), 0);
                            }

                            Log.d("CCC", "Checker " + temp.getString(Message.COL_MEDIA).getBytes().length);
                            byte[] media = Base64.decode(temp.getString(Message.COL_MEDIA), 0);
                            //imageView.setImageBitmap(BitmapFactory.decodeByteArray(z, 0, z.length));
                            Log.d("CCC", "Number Result " + result.length());
                            //Log.d("CCC", z.length + "Size");

                            addMessage(pref.getInt(User.COL_USER_ID, -1) == room_message.getSender_id(), temp.getString(User.COL_DISPLAY_NAME), room_message.getDate_created().getTime().toString(), temp.getString(Message.COL_MESSAGE_TYPE), temp.getString(Message.COL_MESSAGE), media);

                        }
//                        chatViewRoom.addMessages(messages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressBarChatRoom.setVisibility(View.GONE);
                mqttHelper.unsubscribe(topic);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private boolean interval = true;

    private void makeVibrationOrSound() {
        if (interval) {
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audio != null) {
                switch (audio.getRingerMode()) {
                    case AudioManager.RINGER_MODE_NORMAL:
                        myUtil.makeSound(this);
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        myUtil.makeVibration(this, myUtil.VIBRATE_SHORT);
                        break;
                }
            }
        }

        interval = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                interval = true;
            }
        }, 3000);
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

    private boolean isNetworkAvailable() {
        //method to check internet connection
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void chatView() {

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        chatView = findViewById(R.id.chatView);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            chatView.showSenderLayout(false);

        chatView.setOnClickSendButtonListener(new com.shrikanthravi.chatview.widget.ChatView.OnClickSendButtonListener() {
            @Override
            public void onSendButtonClick(String s) {

                if (!s.isEmpty()) {
                    Calendar calendar = Calendar.getInstance();
                    String header = MqttHeader.SEND_ROOM_MESSAGE;
                    //String topic = header + "/room" + chatRoom.getRoom_id();
                    Message message = new Message();
                    message.setSender_id(pref.getInt(User.COL_USER_ID, -1));
                    message.setDate_created(calendar);
                    //message.setMessage(chatRoom.encryptMessage(chatViewRoom.getTypedMessage()));
                    message.setMessage(s);
                    message.setRoom_id(chatRoom.getRoom_id());
                    message.setMessage_type("Text");
                    message.setSender_name(pref.getString(User.COL_DISPLAY_NAME, ""));
                    message.setMedia(null);

                    chatMqttHelper.publish(topic, header, message);

                    com.shrikanthravi.chatview.data.Message message1 = new com.shrikanthravi.chatview.data.Message();
                    message1.setUserName(pref.getString(User.COL_DISPLAY_NAME, ""));
                    message1.setTime(calendar.getTime().toString());
                    message1.setBody(s);
                    message1.setType(com.shrikanthravi.chatview.data.Message.RightSimpleMessage);
                    chatView.addMessage(message1);
                }
            }
        });

        chatView.setOnClickCameraButtonListener(new com.shrikanthravi.chatview.widget.ChatView.OnClickCameraButtonListener() {
            @Override
            public void onCameraButtonClicked() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        chatView.setOnClickGalleryButtonListener(new com.shrikanthravi.chatview.widget.ChatView.OnClickGalleryButtonListener() {
            @Override
            public void onGalleryButtonClick() {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap;
            switch (requestCode) {
                case REQUEST_CAMERA:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    sendMessage("Empty", convertBitmapToByteArray(bitmap), "Image");

                    Uri uri2 = getImageUri(this, bitmap);
                    com.shrikanthravi.chatview.data.Message message2 = new com.shrikanthravi.chatview.data.Message();
                    List<Uri> mSelected2 = new ArrayList<>();
                    mSelected2.add(uri2);
                    //message.setTime();
                    message2.setType(com.shrikanthravi.chatview.data.Message.RightSingleImage);
                    message2.setImageList(mSelected2);
                    message2.setUserName(pref.getString(User.COL_DISPLAY_NAME, ""));
                    chatView.addMessage(message2);
                    break;
                case REQUEST_GALLERY:
                    Uri uri = data.getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        sendMessage("Empty", convertBitmapToByteArray(bitmap), "Image");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    com.shrikanthravi.chatview.data.Message message = new com.shrikanthravi.chatview.data.Message();
                    List<Uri> mSelected = new ArrayList<>();
                    mSelected.add(uri);
                    //message.setTime();
                    message.setType(com.shrikanthravi.chatview.data.Message.RightSingleImage);
                    message.setImageList(mSelected);
                    message.setUserName(pref.getString(User.COL_DISPLAY_NAME, ""));
                    chatView.addMessage(message);
                    break;
            }
        }

    }

    private void addMessage(boolean isUserMessage, String username, String time, String type, String messageReceived, byte[] bytes) {
        com.shrikanthravi.chatview.data.Message message = new com.shrikanthravi.chatview.data.Message();
        message.setTime(time);
        message.setUserName(username);

        if (type.equals("Text")) {
            if (isUserMessage) {
                message.setType(com.shrikanthravi.chatview.data.Message.RightSimpleMessage);
            } else {
                message.setType(com.shrikanthravi.chatview.data.Message.LeftSimpleMessage);
            }

            message.setBody(messageReceived);
        } else {

            if (isUserMessage) {
                message.setType(com.shrikanthravi.chatview.data.Message.RightSingleImage);
            } else {
                message.setType(com.shrikanthravi.chatview.data.Message.LeftSingleImage);
            }
            List<Uri> uriList = new ArrayList<>();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            uriList.add(getImageUri(this, bitmap));
            message.setImageList(uriList);
        }

        chatView.addMessage(message);
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void sendMessage(String messageSend, byte[] media, String type) {
        Calendar calendar = Calendar.getInstance();
        String header = MqttHeader.SEND_ROOM_MESSAGE;
        //String topic = header + "/room" + chatRoom.getRoom_id();
        Message message = new Message();
        message.setSender_id(pref.getInt(User.COL_USER_ID, -1));
        message.setDate_created(calendar);
        //message.setMessage(chatRoom.encryptMessage(chatViewRoom.getTypedMessage()));
        message.setMessage(messageSend);
        message.setRoom_id(chatRoom.getRoom_id());
        message.setMessage_type(type);
        message.setSender_name(pref.getString(User.COL_DISPLAY_NAME, ""));
        message.setMedia(media);

        chatMqttHelper.publish(topic, header, message);
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    private byte[] getBytes(Uri uri) {

        try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = getContentResolver().openInputStream(uri).read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

}
