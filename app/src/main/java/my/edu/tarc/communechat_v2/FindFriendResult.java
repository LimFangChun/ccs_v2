package my.edu.tarc.communechat_v2;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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
import java.util.Random;

import my.edu.tarc.communechat_v2.Adapter.FindResultAdapter;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

public class FindFriendResult extends AppCompatActivity {

    private ListView listViewResult;
    private SharedPreferences pref;
    private ArrayList<Student> resultList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend_result);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listViewResult = findViewById(R.id.listView_findResult);
        progressBar = findViewById(R.id.progressBar_findResult);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

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
        student.setLast_longitude(pref.getLong(User.COL_LAST_LONGITUDE, -1));
        student.setLast_latitude(pref.getLong(User.COL_LAST_LATITUDE, -1));

        int type = getIntent().getIntExtra("Type", -1);
        if (type > 4 || type < 0) {
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
            case 4://location
                header = MqttHeader.FIND_BY_LOCATION;
                setTitle("Find by location");
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
                String[] response = new String[1];
                response[0] = "We couldn't find any users";
                ArrayAdapter adapter = new ArrayAdapter<String>(FindFriendResult.this,
                        android.R.layout.simple_list_item_1, response);
                listViewResult.setAdapter(adapter);
            } else {
                resultList = new ArrayList<>();
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

                        if (!temp.isNull(User.COL_DISTANCE)) {
                            friend.setDistance(Double.parseDouble(temp.getString(User.COL_DISTANCE)));
                        }
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
            //remove progress bar
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };
}
