package my.edu.tarc.communechat_v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.User;

public class LoginActivity extends AppCompatActivity {

    private MqttHelper helper;

    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;
    private static final long TASK_TIMEOUT = 10000;//10 seconds
    private static final String TOPIC_PREFIX = "MY/TARUC/CCS/000000001/";
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

        progressDialog  = new ProgressDialog(LoginActivity.this);
        alertDialog = new AlertDialog.Builder(LoginActivity.this);

        helper = new MqttHelper();
        helper.connect(getApplicationContext());

        uniqueTopic = TOPIC_PREFIX + UUID.randomUUID().toString().substring(0, 8);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();editor.apply();
        if (pref != null) {
            checkLogin(pref.getBoolean("authentication", false));
        }

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("MessageEvent"));

        //Initialize view
        etPassword = (EditText) findViewById(R.id.editText_password);
        etUsername = (AutoCompleteTextView) findViewById(R.id.editText_username);
        btnLogin = (Button) findViewById(R.id.button_login);
        buttonRegister = (Button)findViewById(R.id.button_register);

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
                    alertDialog.setMessage("Please enter your username and password");
                    alertDialog.setTitle("Notice");
                    alertDialog.setNeutralButton("OK", null);
                    alertDialog.show();
                } else if (!isNetworkAvailable()) {
                    alertDialog.setMessage("No internet connection. Please try again");
                    alertDialog.setTitle("Notice");
                    alertDialog.setNeutralButton("OK", null);
                    alertDialog.show();
                }else{
                    user.setUsername(username);
                    user.setPassword(password);

                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    helper.connect(getApplicationContext());
                    helper.publish(uniqueTopic, MqttHeader.LOGIN, user);
                    helper.subscribe(uniqueTopic);
                    helper.getMqttClient().setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.i("[MqttHelper]", "Topic arrive: " + topic);
                            Log.i("[MqttHelper]", "Message arrive: " + message);

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            helper.decode(message.toString());
                            Log.i("[MqttHelper]", "Received header: " + helper.getReceivedHeader());
                            Log.i("[MqttHelper]", "Received result: " + helper.getReceivedResult());
                            if (helper.getReceivedHeader().equals(MqttHeader.LOGIN_REPLY) &&
                                    helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                                alertDialog.setTitle("Wrong username or password");
                                alertDialog.setMessage("Please check if you have typed correct username and password");
                                alertDialog.setNeutralButton("OK", null);
                                alertDialog.show();
                            } else {
                                try{
                                    JSONArray userData = new JSONArray(helper.getReceivedResult());
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

                                    editor.commit();

                                    finish();
                                }catch (JSONException |NullPointerException e){
                                    e.printStackTrace();
                                }
                                helper.unsubscribe(uniqueTopic);
                                helper.disconnect();
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }
            }
        });
    }

    private void checkLogin(boolean status) {
        if (status) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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

//    private class AuthenticationTask extends AsyncTask<Void, Void, Integer> {
//
//        String username;
//        String password;
//
//        MqttMessageHandler handler = new MqttMessageHandler();
//
//        private AuthenticationTask(String username, String password) {
//            this.username = username;
//            this.password = password;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            User loginUser = new User();
//            loginUser.setUsername(username);
//            loginUser.setPassword(password);
//            handler.encode(MqttHeader.LOGIN, loginUser);
//
//            //Random ClientID and Topic generated before log in.
//            //Subscribed to random topic to listen to server.
//            //MqttHelper.startMqtt(getBaseContext());
//            MqttHelper.subscribe(uniqueTopic);
//            MqttHelper.publish(uniqueTopic, handler.getPublish());
//        }
//
//        @Override
//        protected Integer doInBackground(Void... voids) {
//            int result = 0;
//            if (!isCancelled() && isNetworkAvailable()) {
//                try {
//                    Thread.sleep(2000);
//                    if (!message.isEmpty()) {
//                        handler.setReceived(message);
//                        message = "";
//                        if (handler.isLoginAuthenticated()) {
//                            user = handler.getUserData();
//                        }
//                        //Unsubscribe the unique topic used to do the login authentication.
//                        //since the user has log on, the client ID and Topic will be discarded.
//                        MqttHelper.unsubscribe(uniqueTopic);
//                    } else {
//                        this.doInBackground();
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            } else{
//                result = -1;
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
//            progressBar.setVisibility(View.GONE);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            if (integer == 0) {
//                goToMain();
//            }else if (!isNetworkAvailable()){
//                Toast.makeText(LoginActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
//            }
//            else if (integer == -1) {
//                Toast.makeText(LoginActivity.this, R.string.wrong_username_pass, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

}
