package my.edu.tarc.communechat_v2;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import my.edu.tarc.communechat_v2.Adapter.RemovePeopleAdapter;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.*;

public class RemovePeopleFromChatActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private ProgressBar progressBarRemovePeople;
    private ListView listViewParticipantList;
    private Participant participant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_people_from_chat);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        progressBarRemovePeople = findViewById(R.id.progressBar_removePeople);
        progressBarRemovePeople.setVisibility(View.VISIBLE);
        listViewParticipantList = findViewById(R.id.listView_participantList);
        participant = new Participant();
        participant.setRoom_id(getIntent().getIntExtra(Chat_Room.COL_ROOM_ID, -1));
        participant.setUser_id(pref.getInt(User.COL_USER_ID, -1));

        initializeListView();
    }

    private void initializeListView() {
        String header = MqttHeader.GET_PARTICIPANT_LIST_REMOVE;
        String topic = header + "/" + participant.getRoom_id();
        mqttHelper.connectPublishSubscribe(this, topic, header, participant);
        mqttHelper.getMqttClient().setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                MqttHelper helper = new MqttHelper();
                helper.decode(message.toString());
                if (helper.getReceivedHeader().equals(MqttHeader.GET_PARTICIPANT_LIST_REMOVE_REPLY)) {
                    mqttHelper.unsubscribe(topic);
                    if (helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RemovePeopleFromChatActivity.this);
                        builder.setTitle(R.string.no_result);
                        builder.setMessage(R.string.remove_participant_from_chat_desc);
                        builder.setNeutralButton(R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
                        builder.show();
                    } else {
                        processResult(helper.getReceivedResult());
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void processResult(String receivedResult) {
        try {
            JSONArray result = new JSONArray(receivedResult);
            ArrayList<User> resultList = new ArrayList<>();
            for (int i = 0; i <= result.length() - 1; i++) {
                JSONObject temp = result.getJSONObject(i);
                User user = new User();
                user.setUser_id(temp.getInt(User.COL_USER_ID));
                user.setDisplay_name(temp.getString(User.COL_DISPLAY_NAME));
                user.setLast_online(temp.getString(User.COL_LAST_ONLINE));
                user.setStatus(temp.getString(User.COL_STATUS));

                resultList.add(user);
            }
            RemovePeopleAdapter adapter = new RemovePeopleAdapter(
                    RemovePeopleFromChatActivity.this,
                    R.layout.adapter_remove_people_from_group,
                    resultList,
                    participant);
            listViewParticipantList.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            progressBarRemovePeople.setVisibility(View.GONE);
        }
    }
}
