package my.edu.tarc.communechat_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private TextView textViewHello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHello = (TextView)findViewById(R.id.textViewHello);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (pref == null || pref.getString("UID", null) == null){
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        }else{
            textViewHello.setText("UID: "+ pref.getInt("UID", 0));
        }
    }
}
