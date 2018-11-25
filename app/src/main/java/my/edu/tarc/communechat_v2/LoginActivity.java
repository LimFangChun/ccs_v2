package my.edu.tarc.communechat_v2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.RSA;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private AlertDialog.Builder alertDialog;
    private static final long TASK_TIMEOUT = 10000;//10 seconds

    //Views
    private EditText etPassword;
    private EditText etUsername;
    private Button btnLogin;
    private Button buttonRegister;
    private ConstraintLayout layoutLogin;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private User user = new User();
    private String uniqueTopic;

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            message = intent.getStringExtra("message");
//        }
//    };


    private boolean doubleBackTap = false;

    public LoginActivity() throws Exception {
    }

    @Override
    public void onBackPressed() {
        //override this method to prevent user from going back to Main activity without login

        //if user has clicked back button twice, finish the application
        if (doubleBackTap) {
            this.finishAffinity();
            return;
        }

        //feedback to user
        Toast.makeText(LoginActivity.this, R.string.exit_login, Toast.LENGTH_LONG).show();

        //set the double tap to true on first click
        doubleBackTap = true;

        //a delay that will set double tap back to false after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackTap = false;
            }
        }, 3000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize
        progressBar = findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.INVISIBLE);
        alertDialog = new AlertDialog.Builder(LoginActivity.this);
        layoutLogin = findViewById(R.id.layout_login);

        //animations for layoutLogin
        //slide in on initial load
        Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_down);
        layoutLogin.startAnimation(slideIn);

        //establish connection for future use
        mqttHelper.connect(getApplicationContext());

        //generate a random topic
        uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        editor.apply();

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("MessageEvent"));

        //Initialize view
        etPassword = (EditText) findViewById(R.id.editText_password);
        etUsername = (EditText) findViewById(R.id.editText_username);
        btnLogin = (Button) findViewById(R.id.button_login);
        buttonRegister = (Button) findViewById(R.id.button_register);

        setTitle(getString(R.string.login));

        //done by 1st generation
        //again, we don't what is this
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    //Hide IME
                    etPassword.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputManager != null) {
                        inputManager.toggleSoftInput(0, 0);
                    }

                    btnLogin.performClick();
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    //check for empty input
                    alertDialog.setMessage(R.string.empty_username_password);
                    alertDialog.setTitle(R.string.notice);
                    alertDialog.setNeutralButton(R.string.ok, null);
                    alertDialog.show();
                } else if (!isNetworkAvailable()) {
                    //check internet connection
                    alertDialog.setMessage(R.string.no_internet_connection);
                    alertDialog.setTitle(R.string.notice);
                    alertDialog.setNeutralButton(R.string.ok, null);
                    alertDialog.show();
                } else {
                    user.setUsername(username);
                    user.setPassword(password);

                    //make progress bar visible for user feedback
                    progressBar.setVisibility(View.VISIBLE);
                    mqttHelper.connectPublishSubscribe(getApplicationContext(),
                            uniqueTopic,
                            MqttHeader.LOGIN,
                            user);
                    mqttHelper.getMqttClient().setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            login(topic, message);
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String topic, MqttMessage message) {
        MqttHelper helper = new MqttHelper();
        helper.decode(message.toString());
        progressBar.setVisibility(View.INVISIBLE);
        //Log.i("[MqttHelper]", "Received header: " + mqttHelper.getReceivedHeader());
        //Log.i("[MqttHelper]", "Received result: " + mqttHelper.getReceivedResult());
        if (helper.getReceivedHeader().equals(MqttHeader.LOGIN_REPLY)) {
            //unsub from the topic
            mqttHelper.unsubscribe(topic);
            if (helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                alertDialog.setTitle(R.string.wrong_username_password);
                alertDialog.setMessage(R.string.check_username_password);
                alertDialog.setNeutralButton(R.string.ok, null);
                alertDialog.show();
            } else {
                try {
                    JSONArray userData = new JSONArray(helper.getReceivedResult());
                    JSONObject temp = userData.getJSONObject(0);

                    //put user data into shared preference
                    editor.putInt(User.COL_USER_ID, temp.getInt(User.COL_USER_ID));
                    editor.putString(User.COL_DISPLAY_NAME, temp.getString(User.COL_DISPLAY_NAME));
                    editor.putString(User.COL_USERNAME, temp.getString(User.COL_USERNAME));
                    editor.putString(User.COL_PASSWORD, temp.getString(User.COL_PASSWORD));
                    editor.putString(User.COL_POSITION, temp.getString(User.COL_POSITION));
                    editor.putString(User.COL_GENDER, temp.getString(User.COL_GENDER));
                    editor.putString(User.COL_NRIC, temp.getString(User.COL_NRIC));
                    editor.putString(User.COL_PHONE_NUMBER, temp.getString(User.COL_PHONE_NUMBER));
                    editor.putString(User.COL_EMAIL, temp.getString(User.COL_EMAIL));
                    editor.putString(User.COL_ADDRESS, temp.getString(User.COL_ADDRESS));
                    editor.putString(User.COL_CITY_ID, temp.getString(User.COL_CITY_ID));
                    editor.putString(User.COL_STATUS, temp.getString(User.COL_STATUS));
                    editor.putString(User.COL_LAST_ONLINE, temp.getString(User.COL_LAST_ONLINE));
                    editor.putString(Student.COL_FACULTY, temp.getString(Student.COL_FACULTY));
                    editor.putString(Student.COL_COURSE, temp.getString(Student.COL_COURSE));

                    //getting a non-string value in JSON object can cause crash
                    //an extra validation is required
                    if (temp.isNull(User.COL_LAST_LONGITUDE)) {
                        editor.putFloat(User.COL_LAST_LONGITUDE, 0);
                    } else {
                        editor.putFloat(User.COL_LAST_LONGITUDE, (float) temp.getDouble(User.COL_LAST_LONGITUDE));
                    }

                    if (temp.isNull(User.COL_LAST_LATITUDE)) {
                        editor.putFloat(User.COL_LAST_LATITUDE, 0);
                    } else {
                        editor.putFloat(User.COL_LAST_LATITUDE, (float) temp.getDouble(User.COL_LAST_LATITUDE));
                    }

                    if (temp.isNull(Student.COL_TUTORIAL_GROUP)) {
                        editor.putInt(Student.COL_TUTORIAL_GROUP, 0);
                    } else {
                        editor.putInt(Student.COL_TUTORIAL_GROUP, temp.getInt(Student.COL_TUTORIAL_GROUP));
                    }

                    editor.putString(Student.COL_INTAKE, temp.getString(Student.COL_INTAKE));

                    if (temp.isNull(Student.COL_TUTORIAL_GROUP)) {
                        editor.putInt(Student.COL_ACADEMIC_YEAR, 0);
                    } else {
                        editor.putInt(Student.COL_ACADEMIC_YEAR, temp.getInt(Student.COL_ACADEMIC_YEAR));
                    }

                    if (editor.commit()) {
                        finish();
                    }

                    //check if RSA keys are generated
                    //generate one if none
                    //setupRSA();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void finish() {
        //nothing big deal here
        //just animation for leaving Login activity
        super.finish();
        if (isFinishing()) {
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
        }
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


    private void setupRSA(){
        if(pref.getString(User.COL_PRIVATE_KEY, "NULL").equals("NULL")) {
            final RSA rsa = new RSA();
            final String pubKeyString = new String(rsa.getPubKey());

            User user = new User();
            user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
            user.setPublic_key(pubKeyString);

            final String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
            SharedPreferences.Editor editor1 = pref.edit();
            editor1.putString(User.COL_PUBLIC_KEY, pubKeyString);
            editor1.putString(User.COL_PRIVATE_KEY, new String(rsa.getPrivateKey()));
            editor1.apply();
            mqttHelper.connectPublish(getApplicationContext(), uniqueTopic, MqttHeader.UPDATE_PUBLIC_KEY, user);
        }
    }
}
