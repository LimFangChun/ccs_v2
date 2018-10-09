package my.edu.tarc.communechat_v2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private AlertDialog.Builder alertDialog;
    private static final long TASK_TIMEOUT = 10000;//10 seconds

    //Views
    private EditText etPassword;
    private AutoCompleteTextView etUsername;
    private Button btnLogin;
    private Button buttonRegister;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar_login);
        alertDialog = new AlertDialog.Builder(LoginActivity.this);

        MainActivity.mqttHelper.connect(getApplicationContext());

        uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();editor.apply();

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("MessageEvent"));

        //Initialize view
        etPassword = (EditText) findViewById(R.id.editText_password);
        etUsername = (AutoCompleteTextView) findViewById(R.id.editText_username);
        btnLogin = (Button) findViewById(R.id.button_login);
        buttonRegister = (Button)findViewById(R.id.button_register);

        setTitle(getString(R.string.login));

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
                    alertDialog.setMessage(R.string.empty_username_password);
                    alertDialog.setTitle(R.string.notice);
                    alertDialog.setNeutralButton(R.string.ok, null);
                    alertDialog.show();
                } else if (!isNetworkAvailable()) {
                    alertDialog.setMessage(R.string.no_internet_connection);
                    alertDialog.setTitle(R.string.notice);
                    alertDialog.setNeutralButton(R.string.ok, null);
                    alertDialog.show();
                }else{
                    user.setUsername(username);
                    user.setPassword(password);

                    progressBar.setVisibility(View.VISIBLE);
                    MainActivity.mqttHelper.connect(getApplicationContext());
                    MainActivity.mqttHelper.publish(uniqueTopic, MqttHeader.LOGIN, user);
                    MainActivity.mqttHelper.subscribe(uniqueTopic);
                    MainActivity.mqttHelper.getMqttClient().setCallback(new MqttCallback() {
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
        Log.i("[MqttHelper]", "Topic arrive: " + topic);
        Log.i("[MqttHelper]", "Message arrive: " + message);

        MainActivity.mqttHelper.decode(message.toString());
        progressBar.setVisibility(View.INVISIBLE);
        Log.i("[MqttHelper]", "Received header: " + MainActivity.mqttHelper.getReceivedHeader());
        Log.i("[MqttHelper]", "Received result: " + MainActivity.mqttHelper.getReceivedResult());
        if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.LOGIN_REPLY) &&
                MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {

            alertDialog.setTitle(R.string.wrong_username_password);
            alertDialog.setMessage(R.string.check_username_password);
            alertDialog.setNeutralButton(R.string.ok, null);
            alertDialog.show();
        } else {
            try{
                JSONArray userData = new JSONArray(MainActivity.mqttHelper.getReceivedResult());
                JSONObject temp = userData.getJSONObject(0);

                editor.putInt(User.COL_USER_ID, temp.getInt(User.COL_USER_ID));
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
                editor.putInt(Student.COL_TUTORIAL_GROUP, temp.getInt(Student.COL_TUTORIAL_GROUP));
                editor.putInt(Student.COL_INTAKE, temp.getInt(Student.COL_INTAKE));
                editor.putInt(Student.COL_ACADEMIC_YEAR, temp.getInt(Student.COL_ACADEMIC_YEAR));

                editor.commit();
                MainActivity.mqttHelper.unsubscribe(uniqueTopic);
                finish();
            }catch (JSONException |NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
