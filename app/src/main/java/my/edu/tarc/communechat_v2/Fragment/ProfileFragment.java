package my.edu.tarc.communechat_v2.Fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.ProfileActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.TestEncryptionActivity;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class ProfileFragment extends Fragment {
	private String uniqueTopic;
	TextView textViewCourseYearGroupIntake ;
	TextView textViewDisplayName ;
	TextView textViewStudentId ;
	TextView textViewFaculty ;
	ProgressBar progressBar;
	RelativeLayout relativeLayoutProfile;
	CardView cardViewStudent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        relativeLayoutProfile = view.findViewById(R.id.layout_profile);
        cardViewStudent = view.findViewById(R.id.cardView_student);
		progressBar = view.findViewById(R.id.progressBar_profile);
        //TODO: remove test buttons after integrate

        Button button_activity = view.findViewById(R.id.button_activity);
        button_activity.setOnClickListener(buttonListener);
        Button button_activiy1 = view.findViewById(R.id.button_activity1);
        button_activiy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TestEncryptionActivity.class);
                startActivity(intent);
            }
        });
		textViewCourseYearGroupIntake = view.findViewById(R.id.textView_courseYearGroupIntake);
		textViewDisplayName = view.findViewById(R.id.textView_displayName);
		textViewStudentId = view.findViewById(R.id.textView_studentID);
		textViewFaculty = view.findViewById(R.id.textView_faculty);


        return view;
    }

	@Override
	public void onStart() {
		super.onStart();
		progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
		if(getActivity().getClass().equals(MainActivity.class)){
			progressBar.setVisibility(View.GONE);
			//TODO: load my own profile
		}
		else{
			relativeLayoutProfile.setVisibility(View.INVISIBLE);
			cardViewStudent.setVisibility(View.INVISIBLE);
			uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
			Intent passedIntent = getActivity().getIntent();
			int user_id = passedIntent.getIntExtra(User.COL_USER_ID, -1);
			User user = new User();
			user.setUser_id(user_id);
			//load profile from intent
			mqttHelper.connectPublishSubscribe(getContext(), uniqueTopic, MqttHeader.GET_USER_PROFILE, user);
			mqttHelper.getMqttClient().setCallback(mqttCallback);
		}
	}

	private Button.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            startActivity(intent);
        }
    };

    private MqttCallback mqttCallback = new MqttCallback() {
		@Override
		public void connectionLost(Throwable cause) {

		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			mqttHelper.decode(message.toString());
			if(mqttHelper.getReceivedHeader().equals(MqttHeader.GET_USER_PROFILE_REPLY)) {
				try{
					JSONArray result = new JSONArray(mqttHelper.getReceivedResult());
					JSONObject temp = result.getJSONObject(0);
					User user1 = new User();
					user1.setDisplay_name(temp.getString(User.COL_DISPLAY_NAME));
					Student student = new Student();
					student.setAcademic_year(temp.getInt(Student.COL_ACADEMIC_YEAR));
					student.setCourse(temp.getString(Student.COL_COURSE));
					student.setFaculty(temp.getString(Student.COL_FACULTY));
					student.setIntake(temp.getString(Student.COL_INTAKE));
					student.setStudent_id(temp.getString(Student.COL_STUDENT_ID));
					student.setTutorial_group(temp.getInt(Student.COL_TUTORIAL_GROUP));

					textViewDisplayName.setText(user1.getDisplay_name());
					textViewStudentId.setText(student.getStudent_id());
					textViewFaculty.setText(student.getFaculty());
					textViewCourseYearGroupIntake.setText(student.getTutorialGroupString());
				}catch (JSONException | NullPointerException e) {
					e.printStackTrace();
				}
			}
			mqttHelper.unsubscribe(uniqueTopic);
			progressBar.setVisibility(ProgressBar.GONE);
			relativeLayoutProfile.setVisibility(View.VISIBLE);
			cardViewStudent.setVisibility(View.VISIBLE);
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {

		}
	};
}
