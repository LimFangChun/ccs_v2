package my.edu.tarc.communechat_v2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import my.edu.tarc.communechat_v2.Adapter.FriendRequestAdapter;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

public class FriendRequestActivity extends AppCompatActivity {

    private ListView listViewFriendRequest;
    private ProgressBar progressBarFriendRequest;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        listViewFriendRequest = findViewById(R.id.listView_friendRequest);
        progressBarFriendRequest = findViewById(R.id.progressBar_FriendRequest);
        progressBarFriendRequest.setVisibility(View.VISIBLE);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String topic = "friendRequest/" + pref.getInt(User.COL_USER_ID, -1);
        String header = MqttHeader.GET_FRIEND_REQUEST;
        User user = new User();
        user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        MainActivity.mqttHelper.connectPublishSubscribe(getApplicationContext(), topic, header, user);
        MainActivity.mqttHelper.getMqttClient().setCallback(friendRequestCallback);
    }

    private MqttCallback friendRequestCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            MainActivity.mqttHelper.decode(message.toString());
            if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.GET_FRIEND_REQUEST_REPLY)) {
                if (MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                    setTitle("No result");
                    String[] response = new String[1];
                    response[0] = "We couldn't find any users";
                    ArrayAdapter adapter = new ArrayAdapter<String>(FriendRequestActivity.this,
                            android.R.layout.simple_list_item_1, response);
                    listViewFriendRequest.setAdapter(adapter);
                } else {
                    ArrayList<Student> resultList = new ArrayList<>();
                    try {
                        JSONArray result = new JSONArray(MainActivity.mqttHelper.getReceivedResult());
                        for (int i = 0; i <= result.length() - 1; i++) {
                            JSONObject temp = result.getJSONObject(i);

                            Student friend = new Student();
                            friend.setUser_id(temp.getInt(Student.COL_USER_ID));
                            friend.setDisplay_name(temp.getString(Student.COL_DISPLAY_NAME));
                            friend.setStatus(temp.getString(Student.COL_STATUS));
                            friend.setLast_online(temp.getString(Student.COL_LAST_ONLINE));
                            friend.setCourse(temp.getString(Student.COL_COURSE));
                            friend.setAcademic_year(temp.getInt(Student.COL_ACADEMIC_YEAR));
                            friend.setTutorial_group(temp.getInt(Student.COL_TUTORIAL_GROUP));

                            resultList.add(friend);
                        }
                        FriendRequestAdapter adapter = new FriendRequestAdapter(FriendRequestActivity.this,
                                R.layout.adapter_friend_request, resultList);
                        listViewFriendRequest.setAdapter(adapter);
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
            progressBarFriendRequest.setVisibility(View.GONE);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };
}
