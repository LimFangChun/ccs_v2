package my.edu.tarc.communechat_v2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import my.edu.tarc.communechat_v2.Fragment.FriendListFragment;
import my.edu.tarc.communechat_v2.Fragment.ChatFragment;
import my.edu.tarc.communechat_v2.Fragment.FindFriendFragment;
import my.edu.tarc.communechat_v2.internal.MqttHelper;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static my.edu.tarc.communechat_v2.NotificationView.createNotificationChannel;
import static my.edu.tarc.communechat_v2.NotificationView.getChannelID;

public class MainActivity extends AppCompatActivity {

    public static final MqttHelper mqttHelper = new MqttHelper();
    private static final String TAG = MainActivity.class.getSimpleName();


    private SharedPreferences pref;
    private BottomNavigationView bottomNavigationView;
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navi_top_menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_log_out:
                //clear shared preference then navigate user to login activity
                pref.edit().clear().apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //mqttHelper.connect(getApplicationContext());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_bottom);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment())
                    .commit();
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);



    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_chat:
                            selectedFragment = new ChatFragment();
                            break;
                        case R.id.nav_find_friend:
                            selectedFragment = new FindFriendFragment();
                            break;
                        case R.id.nav_add_friend:
                            selectedFragment = new FriendListFragment();
                            break;
                        case R.id.nav_profile:
                            break;


                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mqttHelper.connect(getApplicationContext());
    }




    }


