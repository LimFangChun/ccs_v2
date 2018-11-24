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
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import my.edu.tarc.communechat_v2.Adapter.FindResultAdapter;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class FindFriendResult extends AppCompatActivity {

    private ListView listViewResult;
    private SharedPreferences pref;
    private ArrayList<Student> resultList;
    private ProgressBar progressBar;
    private TextView textViewHeading;

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
        textViewHeading = findViewById(R.id.textView_heading);

        getFriend();
    }

    private void getFriend() {
        Student student = new Student();
        String publishTopic = "find/" + pref.getInt(User.COL_USER_ID, -1);
        String textView_heading = "";
        String header = "";

        student.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        student.setFaculty(pref.getString(Student.COL_FACULTY, ""));
        student.setCourse(pref.getString(Student.COL_COURSE, ""));
        student.setTutorial_group(pref.getInt(Student.COL_TUTORIAL_GROUP, -1));
        student.setIntake(pref.getString(Student.COL_INTAKE, ""));
        student.setAcademic_year(pref.getInt(Student.COL_ACADEMIC_YEAR, -1));
        student.setNric(pref.getString(User.COL_NRIC, ""));
        student.setCity_id(pref.getString(User.COL_CITY_ID, ""));
        student.setLast_longitude((double) pref.getFloat(User.COL_LAST_LONGITUDE, -1));
        student.setLast_latitude((double) pref.getFloat(User.COL_LAST_LATITUDE, -1));

        int type = getIntent().getIntExtra("Type", -1);
        if (type == 5) {
            type = new Random().nextInt(4);
        }

        switch (type) {
            case 0://find by programme
                header = MqttHeader.FIND_BY_PROGRAMME;
                setTitle("Find by programme");
                textView_heading = "Finding friends from " + student.getFaculty();
                break;
            case 1://find by tutorial group
                header = MqttHeader.FIND_BY_TUTORIAL_GROUP;
                setTitle("Find by tutorial group");
                textView_heading = "Finding friends from " + student.getFaculty() + ", " +
                        student.getCourse() + student.getAcademic_year() +
                        " G" + student.getTutorial_group();
                break;
            case 2://find by address
                header = MqttHeader.FIND_BY_ADDRESS;
                setTitle("Find by address");
                textView_heading = "Finding friends nearby your living place";
                break;
            case 3://find by age
                header = MqttHeader.FIND_BY_AGE;
                setTitle("Find by age");
                textView_heading = "Finding friends same age with you";
                break;
            case 4://location/gps
                header = MqttHeader.FIND_BY_LOCATION;
                setTitle("Find by location");
                textView_heading = "Finding friends nearby your current location";
                break;
            case 6:
                header = MqttHeader.ADVANCED_SEARCH;
                setTitle("Advanced search");
                advancedSearch(publishTopic, header);
                return;
        }
        mqttHelper.connectPublishSubscribe(getApplicationContext(),
                publishTopic, header, student);
        mqttHelper.getMqttClient().setCallback(mqttCallback);
        textViewHeading.setText(textView_heading);
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            MqttHelper helper = new MqttHelper();
            helper.decode(message.toString());

            if (helper.getReceivedHeader().equals(MqttHeader.FIND_BY_AGE_REPLY) ||
                    helper.getReceivedHeader().equals(MqttHeader.FIND_BY_ADDRESS_REPLY) ||
                    helper.getReceivedHeader().equals(MqttHeader.FIND_BY_LOCATION_REPLY) ||
                    helper.getReceivedHeader().equals(MqttHeader.FIND_BY_PROGRAMME_REPLY) ||
                    helper.getReceivedHeader().equals(MqttHeader.FIND_BY_TUTORIAL_GROUP_REPLY) ||
                    helper.getReceivedHeader().equals(MqttHeader.ADVANCED_SEARCH_REPLY)) {
                mqttHelper.unsubscribe(topic);
                //remove progress bar
                progressBar.setVisibility(View.GONE);

                if (helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                    String[] response = new String[1];
                    response[0] = "We couldn't find any users";
                    ArrayAdapter adapter = new ArrayAdapter<String>(FindFriendResult.this,
                            android.R.layout.simple_list_item_1, response);
                    listViewResult.setAdapter(adapter);
                } else {
                    resultList = new ArrayList<>();
                    try {
                        JSONArray result = new JSONArray(helper.getReceivedResult());
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
                            friend.setLast_longitude(temp.getDouble(User.COL_LAST_LONGITUDE));
                            friend.setLast_latitude(temp.getDouble(User.COL_LAST_LATITUDE));

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
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private void advancedSearch(String topic, String header) {
        String faculty = getIntent().getStringExtra(Student.COL_FACULTY);
        String course = getIntent().getStringExtra(Student.COL_COURSE);
        String year = getIntent().getStringExtra(Student.COL_ACADEMIC_YEAR);
        String tutorialGroup = getIntent().getStringExtra(Student.COL_TUTORIAL_GROUP);

        Student student = new Student();
        student.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        student.setFaculty(faculty);
        student.setCourse(course);

        student.setAcademic_year(Integer.parseInt(year));

        student.setTutorial_group(Integer.parseInt(tutorialGroup));

        mqttHelper.connectPublishSubscribe(FindFriendResult.this, topic, header, student);
        mqttHelper.getMqttClient().setCallback(mqttCallback);

        //heading
        String tempHeading = "Finding friends with the following criteria: \n";

        if (course.isEmpty() || !Objects.equals(faculty, "")) {
            tempHeading += "Faculty: " + faculty + "\n";
        } else {
            tempHeading += "Faculty: All\n";
        }

        if (course.isEmpty() || !Objects.equals(course, "")) {
            tempHeading += "Course: " + course + "\n";
        } else {
            tempHeading += "Course: All\n";
        }

        if (year.isEmpty() || !Objects.equals(year, "-1")) {
            tempHeading += "Academic year: " + year + "\n";
        } else {
            tempHeading += "Academic year: All\n";
        }

        if (tutorialGroup.isEmpty() || !Objects.equals(tutorialGroup, "-1")) {
            tempHeading += "Tutorial group: " + tutorialGroup + "\n";
        } else {
            tempHeading += "Tutorial group: All";
        }

        textViewHeading.setText(tempHeading);
    }
}
