package my.edu.tarc.communechat_v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import my.edu.tarc.communechat_v2.internal.MessageService;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.internal.MqttMessageHandler;
import my.edu.tarc.communechat_v2.model.User;

public class LoginActivity extends AppCompatActivity {

    private static final long TASK_TIMEOUT = 10000;
    private static final String TOPIC_PREFIX = "MY/TARUC/CCS/000000001/PUB/USER/";
    //Views
    private EditText etPassword;
    private AutoCompleteTextView etLogin;
    private Button btnLogin;
    private ProgressBar progressBar;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private User user = new User();
    private String uniqueTopic = TOPIC_PREFIX + UUID.randomUUID().toString().substring(0, 8);
    private String message = "";
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra("message");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Start service
        this.startService(new Intent(LoginActivity.this, MessageService.class));

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        if (pref != null) {
            checkLogin(pref.getBoolean("authentication", false));
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("MessageEvent"));

        //Initialize view
        etPassword = (EditText) findViewById(R.id.editText_password);
        etLogin = (AutoCompleteTextView) findViewById(R.id.editText_login);
        btnLogin = (Button) findViewById(R.id.button_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#8f1ffc"), android.graphics.PorterDuff.Mode.MULTIPLY);

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
                    inputManager.toggleSoftInput(0, 0);

                    btnLogin.performClick();
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etLogin.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter login username and password.", Toast.LENGTH_SHORT).show();
                } else {
                    final AuthenticationTask task = new AuthenticationTask(etLogin.getText().toString(), etPassword.getText().toString());
                    task.execute();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (task.getStatus() == AsyncTask.Status.RUNNING) {
                                task.cancel(true);
                                progressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(LoginActivity.this, "Connection timeout", Toast.LENGTH_SHORT).show();
                                //MqttHelper.disconnect();
                                //MqttHelper.unsubscribe(uniqueTopic);
                                resetConnection();
                            }
                        }
                    }, TASK_TIMEOUT);
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

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        editor.putBoolean("authentication", true);

        editor.putInt("uid", user.getUid());
        editor.putString("nickname", user.getNickname());
        editor.putString("username", user.getUsername());
        editor.putString("password", user.getPassword());
        editor.putInt("gender", user.getGender());
        editor.putInt("birth_year", user.getBirth_year());
        editor.putInt("birth_month", user.getBirth_month());
        editor.putInt("birth_day", user.getBirth_day());
        editor.putString("email", user.getEmail());
        editor.putString("phone_number", user.getPhone_number());
        editor.putString("status", user.getStatus());
        editor.putString("address", user.getAddress());
        editor.putString("state", user.getState());
        editor.putString("town", user.getTown());
        editor.putString("postal_code", user.getPostal_code());
        editor.putString("country", user.getCountry());
        editor.putString("student_id", user.getStudent_id());
        editor.putString("faculty", user.getFaculty());
        editor.putString("course", user.getCourse());
        editor.putInt("tutorial_group", user.getTutorial_group());
        editor.putString("intake", user.getIntake());
        editor.putInt("academic_year", user.getAcademic_year());
        editor.commit();
        resetConnection();
        startActivity(intent);
        finish();
    }

    private void resetConnection() {
        //Shutdown the MQTT service to be turn on later.
        this.stopService(new Intent(LoginActivity.this, MessageService.class));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class AuthenticationTask extends AsyncTask<Void, Void, Integer> {

        String username;
        String password;

        MqttMessageHandler handler = new MqttMessageHandler();

        private AuthenticationTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            handler.encode(MqttMessageHandler.MqttCommand.REQ_AUTHENTICATION, new String[]{username, password});

            //Random ClientID and Topic generated before log in.
            //Subscribed to random topic to listen to server.
            //MqttHelper.startMqtt(getBaseContext());
            MqttHelper.subscribe(uniqueTopic);
            MqttHelper.publish(uniqueTopic, handler.getPublish());
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int result = 0;
            if (!isCancelled()) {
                if (isNetworkAvailable()) {
                    try {
                        Thread.sleep(2000);
                        if (!message.isEmpty()) {
                            handler.setReceived(message);
                            message = "";
                            if (handler.mqttCommand == MqttMessageHandler.MqttCommand.ACK_AUTHENTICATION) {
                                if (handler.isLoginAuthenticated() == 3) {
                                    user = handler.getUserData();
                                    Log.i("[Login]", user.getNickname());
                                }
                                result = handler.isLoginAuthenticated();

                                //Unsubscribe the unique topic used to do the login authentication.
                                //since the user has log on, the client ID and Topic will be discarded.
                                MqttHelper.unsubscribe(uniqueTopic);
                            }
                        } else {
                            this.doInBackground();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else
                    result = 4;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (integer == 1) {
                Toast.makeText(LoginActivity.this, "Username is not exists", Toast.LENGTH_SHORT).show();
            } else if (integer == 2) {
                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            } else if (integer == 3) {
                goToMain();
            } else if (integer == 4) {
                Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
            //MqttHelper.disconnect();
            //MqttHelper.unsubscribe(uniqueTopic);
            resetConnection();
        }
    }

}
