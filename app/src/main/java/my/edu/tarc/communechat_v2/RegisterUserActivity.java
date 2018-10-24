package my.edu.tarc.communechat_v2;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.User;

public class RegisterUserActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private AlertDialog.Builder alertDialog;

    //variables for view
    private EditText editTextUsername, editTextPassword;
    private Button buttonRegister, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //assert means telling the program the following attribute is not a null value
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.register_title));

        progressBar = findViewById(R.id.progressBar_register);
        alertDialog = new AlertDialog.Builder(RegisterUserActivity.this);

        //initialize view
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        editTextUsername = (EditText) findViewById(R.id.editText_username);
        buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonRegister = (Button) findViewById(R.id.button_register);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    alertDialog.setTitle(R.string.notice);
                    alertDialog.setMessage(R.string.empty_username_password);
                    alertDialog.setNeutralButton(R.string.ok, null);
                    alertDialog.show();
                } else if (!isNetworkAvailable()) {
                    alertDialog.setTitle(R.string.notice);
                    alertDialog.setMessage(R.string.no_internet_connection);
                    alertDialog.setNeutralButton(R.string.ok, null);
                    alertDialog.show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    String uniqueID = UUID.randomUUID().toString().substring(0, 8);
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    MainActivity.mqttHelper.connectPublishSubscribe(getApplicationContext(),
                            uniqueID, MqttHeader.REGISTER_USER, newUser);
                    MainActivity.mqttHelper.getMqttClient().setCallback(mqttCallback);
                }
            }
        });
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            responseRegister(message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private void responseRegister(MqttMessage message) {
        MainActivity.mqttHelper.decode(message.toString());
        progressBar.setVisibility(View.INVISIBLE);

        if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.REGISTER_USER_REPLY) &&
                MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.DUPLICATED)) {
            alertDialog.setTitle(R.string.duplicated_username);
            alertDialog.setMessage(R.string.try_again_username);
            alertDialog.setNeutralButton(R.string.ok, null);
            alertDialog.show();
        } else if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.REGISTER_USER_REPLY) &&
                MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
            alertDialog.setTitle(R.string.failed);
            alertDialog.setMessage(R.string.failed_register);
            alertDialog.setNeutralButton(R.string.ok, null);
            alertDialog.show();
        } else if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.REGISTER_USER_REPLY) &&
                MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.SUCCESS)) {
            alertDialog.setTitle(R.string.success);
            alertDialog.setMessage(R.string.register_success);
            alertDialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertDialog.show();
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
