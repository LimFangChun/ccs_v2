package my.edu.tarc.communechat_v2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class UpdateProfileActivity extends AppCompatActivity {
	private AlertDialog.Builder alertDialog;
	private SharedPreferences pref;
	private CardView cardViewChangePassword;
	private EditText editTextDisplayName;
	private RadioGroup radioGroupPosition;
	private RadioButton radioButtonStudent;
	private RadioButton radioButtonLecturer;
	private RadioGroup radioGroupGender;
	private RadioButton radioButtonMale;
	private RadioButton radioButtonFemale;
	private EditText editTextDob;
	private EditText editTextPhone;
	private EditText editTextEmail;
	//TODO: addressssssss

	//For student details
	private CardView cardViewStudentDetails;
	private EditText editTextStudentID;
	private Spinner spinnerFaculty;
	private Spinner spinnerCourse;
	private Spinner spinnerTutorialGroup;
	private Spinner spinnerIntake;
	private Spinner spinnerAcademicYear;

	//Calendar for DoB
	private Calendar calendar;
	private Button buttonNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_profile);

		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		//initialize controls
		cardViewChangePassword = findViewById(R.id.cardView_changePassword);
		editTextDisplayName = findViewById(R.id.editText_displayName_config);
		radioGroupPosition = findViewById(R.id.radioGroup_position_config);
		radioButtonStudent = findViewById(R.id.radioButton_student);
		radioButtonLecturer = findViewById(R.id.radioButton_lecturer);
		radioGroupGender = findViewById(R.id.radioGroup_gender_config);
		radioButtonMale = findViewById(R.id.radioButton_male);
		radioButtonFemale = findViewById(R.id.radioButton_female);
		editTextDob = findViewById(R.id.editText_dateOfBirth);
		editTextPhone = findViewById(R.id.editText_phoneNumber);
		editTextEmail = findViewById(R.id.editText_email);

		//For student details
		cardViewStudentDetails = findViewById(R.id.cardView_studentDetails);
		editTextStudentID = findViewById(R.id.editText_studentID);
		spinnerFaculty = findViewById(R.id.spinner_faculty);
		spinnerCourse = findViewById(R.id.spinner_course_profile);
		spinnerTutorialGroup = findViewById(R.id.spinner_tutorialGroup_profile);
		spinnerIntake = findViewById(R.id.spinner_intake_profile);
		spinnerAcademicYear = findViewById(R.id.spinner_academicYear_profile);

		buttonNext = findViewById(R.id.button_next);
		editTextDob.setOnClickListener(showDatePickerDialog);
		calendar = Calendar.getInstance();

		alertDialog = new AlertDialog.Builder(UpdateProfileActivity.this);
		buttonNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (saveProfile()) {
					updateUser();
					finish();
				}
			}
		});
		radioButtonStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					cardViewStudentDetails.setVisibility(View.VISIBLE);
				} else {
					cardViewStudentDetails.setVisibility(View.GONE);
				}
			}
		});
		initializeFacultySpinner();
		initializeProfile();
	}

	private void initializeProfile(User user) {
		//initialize value of controls with data in user
		//TODO: addressssssss
		editTextDisplayName.setText(user.getDisplay_name());

		if (user.getGender().equals(User.GENDER_MALE)) {
			radioButtonMale.setChecked(true);
		} else if (user.getGender().equals(User.GENDER_FEMALE)) {
			radioButtonFemale.setChecked(true);
		}

		if (user.getPosition().equals(User.POSITION_STUDENT)) {
			radioButtonStudent.setChecked(true);
		} else if (user.getPosition().equals(User.POSITION_LECTURER)) {
			radioButtonLecturer.setChecked(true);
		}

		editTextDob.setText(user.getDobString(User.SIMPLE_DATE_FORMAT));
		editTextPhone.setText(user.getPhone_number());
		editTextEmail.setText(user.getEmail());

	}

	private void initializeProfile(Student student) {
		initializeProfile((User) student);
		//initialize student things here
		Log.i("[UpdateProfile]", "Hello world!");
		editTextStudentID.setText(student.getStudent_id());
		if (!editTextStudentID.getText().toString().equals("")) {
			editTextStudentID.setEnabled(false);
			editTextStudentID.setFocusable(false);
		}
		Log.i("[UpdateProfile]", "Hello warudo!");
		spinnerFaculty.setSelection(getSpinnerIndex(spinnerFaculty, student.getFaculty()));
		//Todo: spinners except faculty cannot be initialized
		Log.i("[UpdateProfile]", Integer.toString(getSpinnerIndex(spinnerCourse, student.getCourse())));
		Log.i("[UpdateProfile]", student.getCourse());
		spinnerCourse.setSelection(getSpinnerIndex(spinnerCourse, student.getCourse()),false);
		spinnerTutorialGroup.setSelection(getSpinnerIndex(spinnerTutorialGroup, Integer.toString(student.getTutorial_group())),false);
		spinnerIntake.setSelection(getSpinnerIndex(spinnerIntake, student.getIntake()),false);
		spinnerAcademicYear.setSelection(getSpinnerIndex(spinnerAcademicYear, Integer.toString(student.getAcademic_year())),false);
	}

	private void initializeProfile() {
		if (isStudent())
			initializeProfile(getStudentFromPreferences());
		else
			initializeProfile(getUserFromPreferences());
	}

	private boolean isStudent() {
		String position = pref.getString(User.COL_POSITION, null);
		if (position != null && position.equals(User.POSITION_STUDENT)) {
			return true;
		}
		return false;
	}

	private CardView.OnClickListener showDatePickerDialog = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			new DatePickerDialog(UpdateProfileActivity.this, date, calendar
					.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show();
		}
	};

	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		}

	};

	private void updateLabel() {
		String myFormat = "dd/MM/yy"; //In which you need put here
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

		editTextDob.setText(sdf.format(calendar.getTime()));
	}


	private void initializeFacultySpinner() {
		//TODO: should be change to retrieve from server due to hard coding.
		String[] facultyArray = {"-Tap here-", "FOCS"};
		ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, facultyArray);
		spinnerFaculty.setAdapter(facultyAdapter);
		spinnerFaculty.setSelection(0);

		spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (i == 0) {
					setStudentViewsEnabled(false);
				} else {
					setStudentViewsEnabled(true);
					initializeCourseSpinner(adapterView.getItemAtPosition(i).toString());
					initializeGroupSpinner();
					initializeIntakeSpinner();
					initializeYearSpinner();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}

	private void setStudentViewsEnabled(boolean enabled) {
		spinnerCourse.setEnabled(enabled);
		spinnerTutorialGroup.setEnabled(enabled);
		spinnerIntake.setEnabled(enabled);
		spinnerAcademicYear.setEnabled(enabled);
	}

	private void initializeCourseSpinner(String faculty) {
		String[] courses = {};

		switch (faculty) {
			case "FOCS":
				String[] temp = {"-Tap here-", "RSD", "REI", "RST", "RIT", "RMM"};
				courses = temp;
				break;
		}

		ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, courses);
		spinnerCourse.setAdapter(courseAdapter);
		spinnerCourse.setSelection(0);
	}

	private void initializeIntakeSpinner() {
		String[] intakes = {"-Tap here-", "May", "October"};

		ArrayAdapter<String> intakeAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, intakes);
		spinnerIntake.setAdapter(intakeAdapter);
		spinnerIntake.setSelection(0);
	}

	private void initializeYearSpinner() {
		String[] years = {"-Tap here-", "1", "2", "3", "4"};

		ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, years);
		spinnerAcademicYear.setAdapter(yearAdapter);
		spinnerAcademicYear.setSelection(0);
	}

	private void initializeGroupSpinner() {
		String[] groups = {"-Tap here-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

		ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, groups);
		spinnerTutorialGroup.setAdapter(groupAdapter);
		spinnerTutorialGroup.setSelection(0);
	}

	private void updateUser() {
		String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);

		User user = getUserFromViews();
		if (user != null) {
			user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
			user.setUsername(pref.getString(User.COL_USERNAME, ""));
			if (user.getPosition().equals(User.POSITION_STUDENT))
				updateStudent();
			if (user.getUser_id() != -1) {
				mqttHelper.connectPublish(this, uniqueTopic, MqttHeader.UPDATE_USER, user);
			}
		}
	}

	private void updateStudent() {
		String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);

		Student student = getStudentFromViews();
		if (student != null) {

			student.setUser_id(pref.getInt(User.COL_USER_ID, -1));
			if (student.getUser_id() != -1) {
				mqttHelper.connectPublish(this, uniqueTopic, MqttHeader.UPDATE_STUDENT, student);
			}
		}
	}

	private boolean saveProfile() {
		SharedPreferences.Editor editor = pref.edit();
		User user = getUserFromViews();
		if (user == null) {
			alertDialog.setTitle(R.string.alert_empty_fields);
			alertDialog.setMessage(R.string.alert_empty_fields_message);
			alertDialog.setNeutralButton(R.string.ok, null);
			alertDialog.show();
			return false;
		}
		editor.putString(User.COL_DISPLAY_NAME, user.getDisplay_name());
		editor.putString(User.COL_POSITION, user.getPosition());
		editor.putString(User.COL_GENDER, user.getGender());
		editor.putString(User.COL_DATE_OF_BIRTH, user.getDobString(User.SIMPLE_DATE_FORMAT));
		editor.putString(User.COL_PHONE_NUMBER, user.getPhone_number());
		editor.putString(User.COL_EMAIL, user.getEmail());

		if (user.getPosition().equals(User.POSITION_STUDENT)) {
			Student student = getStudentFromViews();
			if (student == null) {
				alertDialog.setTitle(R.string.alert_empty_fields);
				alertDialog.setMessage(R.string.alert_empty_fields_message);
				alertDialog.setNeutralButton(R.string.ok, null);
				alertDialog.show();
				return false;
			}
			editor.putString(Student.COL_STUDENT_ID, student.getStudent_id());
			editor.putString(Student.COL_FACULTY, student.getFaculty());
			editor.putString(Student.COL_COURSE, student.getCourse());
			editor.putInt(Student.COL_TUTORIAL_GROUP, student.getTutorial_group());
			editor.putString(Student.COL_INTAKE, student.getIntake());
			editor.putInt(Student.COL_ACADEMIC_YEAR, student.getAcademic_year());

		}
		editor.apply();
		return true;
	}


	private User getUserFromViews() {
		User user = new User();

		if (editTextDisplayName.getText().toString().isEmpty()
				|| editTextDob.getText().toString().equals(getString(R.string.date_of_birth))
				|| editTextEmail.getText().toString().isEmpty()
				|| editTextPhone.getText().toString().isEmpty()
				|| radioGroupPosition.getCheckedRadioButtonId() == -1
				|| radioGroupGender.getCheckedRadioButtonId() == -1)
			return null;

		user.setDisplay_name(editTextDisplayName.getText().toString());
		if (radioButtonStudent.isChecked()) {
			user.setPosition(User.POSITION_STUDENT);
		} else {
			user.setPosition(User.POSITION_LECTURER);
		}
		if (radioButtonMale.isChecked()) {
			user.setGender(User.GENDER_MALE);
		} else {
			user.setGender(User.GENDER_FEMALE);
		}
		user.setDobFromString(editTextDob.getText().toString(), User.SIMPLE_DATE_FORMAT);
		user.setPhone_number(editTextPhone.getText().toString());
		user.setEmail(editTextEmail.getText().toString());

		//user.setAddress("");

		return user;
	}

	private Student getStudentFromViews() {
		Student student = new Student();

		if (editTextStudentID.getText().toString().isEmpty()
				|| spinnerFaculty.getSelectedItemPosition() == 0
				|| spinnerCourse.getSelectedItemPosition() == 0
				|| spinnerTutorialGroup.getSelectedItemPosition() == 0
				|| spinnerIntake.getSelectedItemPosition() == 0
				|| spinnerAcademicYear.getSelectedItemPosition() == 0)
			return null;
		student.setStudent_id(editTextStudentID.getText().toString());
		student.setFaculty(spinnerFaculty.getSelectedItem().toString());
		student.setCourse(spinnerCourse.getSelectedItem().toString());
		student.setTutorial_group(Integer.parseInt(spinnerTutorialGroup.getSelectedItem().toString()));
		student.setIntake(spinnerIntake.getSelectedItem().toString());
		student.setAcademic_year(Integer.parseInt(spinnerAcademicYear.getSelectedItem().toString()));

		return student;
	}

	private User getUserFromPreferences() {
		String empty = ""; // to avoid NullPointerException
		User user = new User();
		user.setUsername(pref.getString(User.COL_USERNAME, empty));
		user.setDisplay_name(pref.getString(User.COL_DISPLAY_NAME, empty));
		user.setPosition(pref.getString(User.COL_POSITION, empty));
		user.setPassword(pref.getString(User.COL_PASSWORD, empty));
		user.setDobFromString(pref.getString(User.COL_DATE_OF_BIRTH, empty), User.SIMPLE_DATE_FORMAT);
		user.setGender(pref.getString(User.COL_GENDER, empty));
		user.setPhone_number(pref.getString(User.COL_PHONE_NUMBER, empty));
		user.setEmail(pref.getString(User.COL_EMAIL, empty));
		user.setAddress(pref.getString(User.COL_ADDRESS, empty));
		user.setCity_id(pref.getString(User.COL_CITY_ID, empty));
		// 1,...=user_id, username, display_name, position, password, gender, phone_number, email, address, city_id
		return user;
	}

	private Student getStudentFromPreferences() {
		String empty = ""; // to avoid NullPointerException
		Student student = new Student();
		student.setUsername(pref.getString(User.COL_USERNAME, empty));
		student.setDisplay_name(pref.getString(User.COL_DISPLAY_NAME, empty));
		student.setPosition(pref.getString(User.COL_POSITION, empty));
		student.setPassword(pref.getString(User.COL_PASSWORD, empty));
		student.setDobFromString(pref.getString(User.COL_DATE_OF_BIRTH, empty), User.SIMPLE_DATE_FORMAT);
		student.setGender(pref.getString(User.COL_GENDER, empty));
		student.setPhone_number(pref.getString(User.COL_PHONE_NUMBER, empty));
		student.setEmail(pref.getString(User.COL_EMAIL, empty));
		student.setAddress(pref.getString(User.COL_ADDRESS, empty));
		student.setCity_id(pref.getString(User.COL_CITY_ID, empty));

		student.setStudent_id(pref.getString(Student.COL_STUDENT_ID, empty));
		student.setFaculty(pref.getString(Student.COL_FACULTY, empty));
		student.setCourse(pref.getString(Student.COL_COURSE, empty));
		student.setTutorial_group(pref.getInt(Student.COL_TUTORIAL_GROUP, 0));
		student.setIntake(pref.getString(Student.COL_INTAKE, empty));
		student.setAcademic_year(pref.getInt(Student.COL_ACADEMIC_YEAR, 0));
		return student;
	}

	private int getSpinnerIndex(Spinner spinner, String myString) {
		for (int i = 0; i < spinner.getCount(); i++) {
			if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
				return i;
			}
		}

		return 0;
	}
}