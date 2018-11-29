package my.edu.tarc.communechat_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import my.edu.tarc.communechat_v2.Adapter.ParticipantListAdapter;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class GroupInfoActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private ProgressBar progressBarParticipantList;
    private TextView textViewRoomName;
    private TextView textViewDateCreated;
    private ListView listViewParticipant;
    private CircleImageView imageViewGroupPicture;
    private FloatingActionButton fabChoosePhoto;
    private FloatingActionButton fabUpload;
    private ProgressBar progressBarUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init views
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        progressBarParticipantList = findViewById(R.id.progressBar_participantList);
        progressBarParticipantList.setVisibility(View.VISIBLE);
        textViewDateCreated = findViewById(R.id.textView_dateCreated);
        textViewDateCreated.setVisibility(View.GONE);
        textViewRoomName = findViewById(R.id.textView_roomName);
        textViewRoomName.setVisibility(View.GONE);
        listViewParticipant = findViewById(R.id.listView_participantList);
        imageViewGroupPicture = findViewById(R.id.circleImage_groupPicture);
        imageViewGroupPicture.setVisibility(View.GONE);
        fabChoosePhoto = findViewById(R.id.fab_choosePhoto);
        fabUpload = findViewById(R.id.fab_upload);
        fabUpload.setVisibility(View.GONE);
        progressBarUpload = findViewById(R.id.progressBar_upload);
        progressBarUpload.setVisibility(View.GONE);

        Chat_Room chatRoom = new Chat_Room();
        chatRoom.setRoom_id(getIntent().getIntExtra(Chat_Room.COL_ROOM_ID, -1));

        initializeGroupInfo(chatRoom);
        initializeListItemClickListener();
        initializeFabClickListener(chatRoom);
    }

    private void initializeGroupInfo(Chat_Room chat_room) {
        if (chat_room.getRoom_id() == -1) {
            Toast.makeText(this, "Invalid room id", Toast.LENGTH_LONG).show();
            return;
        }

        String topic = "getRoomInfo/" + chat_room.getRoom_id();
        String header = MqttHeader.GET_ROOM_INFO;
        mqttHelper.connectPublishSubscribe(this, topic, header, chat_room);
        mqttHelper.getMqttClient().setCallback(getRoomInfoCallback);
    }

    private MqttCallback getRoomInfoCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            MqttHelper helper = new MqttHelper();
            helper.decode(message.toString());
            if (helper.getReceivedHeader().equals(MqttHeader.GET_ROOM_INFO_REPLY)) {
                if (helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                    Toast.makeText(GroupInfoActivity.this, "No room info", Toast.LENGTH_LONG).show();
                } else {
                    processResult(helper.getReceivedResult());
                }
                imageViewGroupPicture.setVisibility(View.VISIBLE);
                textViewRoomName.setVisibility(View.VISIBLE);
                textViewDateCreated.setVisibility(View.VISIBLE);
                progressBarParticipantList.setVisibility(View.GONE);
                mqttHelper.unsubscribe(topic);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private void processResult(String receivedResult) {
        try {
            JSONArray jsonResult = new JSONArray(receivedResult);
            ArrayList<Participant> participantArrayList = new ArrayList<>();

            for (int i = 0; i <= jsonResult.length() - 1; i++) {
                Participant participant = new Participant();
                JSONObject temp = jsonResult.getJSONObject(i);

                Chat_Room chatRoom = new Chat_Room();
                chatRoom.setRoom_name(temp.getString(Chat_Room.COL_ROOM_NAME));
                setTitle(chatRoom.getRoom_name() + " group info");
                chatRoom.setDate_created(temp.getString(Chat_Room.COL_DATE_CREATED));

                textViewRoomName.setText(chatRoom.getRoom_name());
                String dateCreated = "Date created:\n" + chatRoom.formatDateCreated() + "\n" + chatRoom.calculateDateCreated();
                textViewDateCreated.setText(dateCreated);

                participant.setUser_id(temp.getInt(Participant.COL_USER_ID));
                participant.setDisplay_name(temp.getString(Participant.COL_DISPLAY_NAME));
                participant.setRole(temp.getString(Participant.COL_ROLE));
                participant.setLast_online(temp.getString(Participant.COL_LAST_ONLINE));
                participant.setStatus(temp.getString(Participant.COL_STATUS));

                participantArrayList.add(participant);
            }
            ParticipantListAdapter adapter = new ParticipantListAdapter(GroupInfoActivity.this, R.layout.adapter_participant_list, participantArrayList);
            listViewParticipant.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeListItemClickListener() {
        listViewParticipant.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textViewUserID = view.findViewById(R.id.textView_userID);
                int userID = Integer.parseInt(textViewUserID.getText().toString());

                Intent intent = new Intent(GroupInfoActivity.this, ProfileActivity.class);
                intent.putExtra(User.COL_USER_ID, userID);
                startActivity(intent);
            }
        });
    }

    private static final int SELECT_PICTURE_CODE = 1;
    private Uri filepath;

    private void initializeFabClickListener(final Chat_Room chatRoom) {
        //todo test these
        fabChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_PICTURE_CODE);
            }
        });

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
//                imageViewGroupPicture.setImageBitmap(bitmap);
                fabUpload.setVisibility(View.VISIBLE);
               // Picasso.get().load(data.getData()).into(imageViewGroupPicture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
