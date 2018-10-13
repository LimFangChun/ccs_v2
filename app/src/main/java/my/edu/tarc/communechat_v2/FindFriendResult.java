package my.edu.tarc.communechat_v2;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

import my.edu.tarc.communechat_v2.Adapter.FindResultAdapter;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Friendship;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

public class FindFriendResult extends AppCompatActivity {

    private ListView listViewResult;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend_result);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listViewResult = findViewById(R.id.listView_findResult);
        listViewResult.setOnItemClickListener(listViewListener);

        getFriend();
    }

    private void getFriend() {
        Student student = new Student();
        String publishTopic = "find/" + pref.getInt(User.COL_USER_ID, -1);
        String header = "";

        student.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        student.setFaculty(pref.getString(Student.COL_FACULTY, ""));
        student.setCourse(pref.getString(Student.COL_COURSE, ""));
        student.setTutorial_group(pref.getInt(Student.COL_TUTORIAL_GROUP, -1));
        student.setIntake(pref.getString(Student.COL_INTAKE, ""));
        student.setAcademic_year(pref.getInt(Student.COL_ACADEMIC_YEAR, -1));
        student.setNric(pref.getString(User.COL_NRIC, ""));
        student.setCity_id(pref.getString(User.COL_CITY_ID, ""));
        int type = getIntent().getIntExtra("Type", -1);
        if (type > 3 || type < 0) {
            type = new Random().nextInt(3);
        }
        switch (type) {
            case 0://find by programme
                header = MqttHeader.FIND_BY_PROGRAMME;
                setTitle("Find by programme");
                break;
            case 1://find by tutorial group
                header = MqttHeader.FIND_BY_TUTORIAL_GROUP;
                setTitle("Find by tutorial group");
                break;
            case 2://find by address
                header = MqttHeader.FIND_BY_ADDRESS;
                setTitle("Find by address");
                break;
            case 3://find by age
                header = MqttHeader.FIND_BY_AGE;
                setTitle("Find by age");
                break;
        }
        MainActivity.mqttHelper.connectPublishSubscribe(getApplicationContext(),
                publishTopic, header, student);
        MainActivity.mqttHelper.getMqttClient().setCallback(mqttCallback);
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            MainActivity.mqttHelper.decode(message.toString());

            if (MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                setTitle("No result");
                String[] response = new String[1];
                response[0] = "Hmmm...Something went wrong\nWe couldn't find any users";
                ArrayAdapter adapter = new ArrayAdapter<String>(FindFriendResult.this,
                        android.R.layout.simple_list_item_1, response);
                listViewResult.setAdapter(adapter);
            } else {
                ArrayList<Student> resultList = new ArrayList<>();
                try {
                    JSONArray result = new JSONArray(MainActivity.mqttHelper.getReceivedResult());
                    for (int i = 0; i < result.length() - 1; i++) {
                        Student friend = new Student();
                        friend.setUser_id(result.getJSONObject(i).getInt(Student.COL_USER_ID));
                        friend.setDisplay_name(result.getJSONObject(i).getString(Student.COL_DISPLAY_NAME));
                        friend.setStatus(result.getJSONObject(i).getString(Student.COL_STATUS));
                        friend.setLast_online(result.getJSONObject(i).getString(Student.COL_LAST_ONLINE));
                        friend.setCourse(result.getJSONObject(i).getString(Student.COL_COURSE));
                        friend.setAcademic_year(result.getJSONObject(i).getInt(Student.COL_ACADEMIC_YEAR));
                        friend.setTutorial_group(result.getJSONObject(i).getInt(Student.COL_TUTORIAL_GROUP));

                        resultList.add(friend);
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }

                //put all result into custom list
                FindResultAdapter adapter = new FindResultAdapter(FindFriendResult.this,
                        R.layout.adapter_find_result,
                        resultList);
                listViewResult.setAdapter(adapter);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private static Student temp = new Student();
    private ProgressBar progressBarAdd;
    private TextView textViewFriendID;
    private ListView.OnItemClickListener listViewListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            textViewFriendID = view.findViewById(R.id.textView_userID);
            TextView textViewDisplayName = view.findViewById(R.id.textView_username);
            progressBarAdd = view.findViewById(R.id.progressBar_addFriend);
            progressBarAdd.setVisibility(View.VISIBLE);

            Friendship friendship = new Friendship();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            friendship.setUser_id(pref.getInt(User.COL_USER_ID, -1));
            friendship.setFriend_id(Integer.parseInt(textViewFriendID.getText().toString()));

            temp.setUser_id(friendship.getUser_id());
            temp.setDisplay_name(textViewDisplayName.getText().toString());

            String topic = "addFriend/" + friendship.getUser_id();
            String header = MqttHeader.REQ_ADD_FRIEND;
            MainActivity.mqttHelper.connectPublishSubscribe(getApplicationContext(), topic, header, friendship);
            MainActivity.mqttHelper.getMqttClient().setCallback(addFriendCallback);
        }
    };

    private MqttCallback addFriendCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            MainActivity.mqttHelper.decode(message.toString());
            if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.REQ_ADD_FRIEND_REPLY)) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FindFriendResult.this);
                if (MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.SUCCESS)) {
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Friend request has sent to " + temp.getDisplay_name());
                    alertDialog.setNeutralButton(R.string.ok, null);

                    //remove added friend from the list

                } else {
                    alertDialog.setTitle("Failed");
                    alertDialog.setMessage("Failed to add " + temp.getDisplay_name() + " as friend");
                    alertDialog.setNeutralButton(R.string.ok, null);
                }
                alertDialog.show();

                for(int i=0;i<listViewResult.getCount()-1;i++){
                    if(Integer.parseInt(textViewFriendID.getText().toString()) == temp.getUser_id()){
                        listViewResult.removeViewAt(i);
                    }
                }
            }
            MainActivity.mqttHelper.unsubscribe(topic);
            progressBarAdd.setVisibility(View.GONE);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };
}
