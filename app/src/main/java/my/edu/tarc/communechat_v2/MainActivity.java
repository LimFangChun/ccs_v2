package my.edu.tarc.communechat_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.User;

public class MainActivity extends AppCompatActivity {

    private MqttHelper mqttHelper;

    private SharedPreferences pref;
    private TextView textViewHello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttHelper = new MqttHelper();

        textViewHello = (TextView)findViewById(R.id.textViewHello);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (pref == null ||  pref.getInt(User.COL_USER_ID, 0) == 0){
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        }else{
            textViewHello.setText("user_id: "+ pref.getInt(User.COL_USER_ID, 0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.disconnect();
    }
}
