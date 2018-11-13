package my.edu.tarc.communechat_v2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.edu.tarc.communechat_v2.model.User;

public class UpdateProfileActivity extends AppCompatActivity {
	SharedPreferences pref;
	EditText editTextDob;
	Calendar calendar;
	Button buttonNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_profile);
		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		User myProfile = getUserFromPreferences();
		editTextDob = findViewById(R.id.editText_dateOfBirth);
		buttonNext = findViewById(R.id.button_next);
		editTextDob.setOnClickListener(showDatePickerDialog);
		calendar = Calendar.getInstance();

		buttonNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	private User getUserFromPreferences(){
		User user = new User();
		user.setUsername(pref.getString(User.COL_USERNAME,null));
		user.setDisplay_name(pref.getString(User.COL_DISPLAY_NAME,null));
		user.setPosition(pref.getString(User.COL_POSITION,null));
		user.setPassword(pref.getString(User.COL_PASSWORD,null));
		//add dob column here
		user.setGender(pref.getString(User.COL_GENDER,null));
		user.setPhone_number(pref.getString(User.COL_PHONE_NUMBER,null));
		user.setEmail(pref.getString(User.COL_EMAIL,null));
		user.setAddress(pref.getString(User.COL_ADDRESS,null));
		user.setCity_id(pref.getString(User.COL_CITY_ID,null));
		// 1,...=user_id, username, display_name, position, password, gender, phone_number, email, address, city_id
		return user;
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
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		editTextDob.setText(sdf.format(calendar.getTime()));
	}

}
