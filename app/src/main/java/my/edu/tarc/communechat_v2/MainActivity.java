package my.edu.tarc.communechat_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.UUID;

import my.edu.tarc.communechat_v2.Fragment.ChatFragment;
import my.edu.tarc.communechat_v2.Fragment.FindFriendFragment;
import my.edu.tarc.communechat_v2.Fragment.FriendListFragment;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.User;

public class MainActivity extends AppCompatActivity {

    public static final MqttHelper mqttHelper = new MqttHelper();

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
                //TODO: intent to setting activity
                break;
            case R.id.nav_log_out:
                //clear shared preference then navigate user to login activity
                updateUserStatus("Offline");
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

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_bottom);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment())
                    .commit();
        }

        //check user login status
        //if not logged in, redirect user to login activity
        if (pref == null || pref.getInt(User.COL_USER_ID, -1) == -1) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
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

        updateUserStatus("Offline");
        mqttHelper.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus("Online");
    }

    private void updateUserStatus(String status) {
        String topic = UUID.randomUUID().toString().substring(0, 8);
        String header = MqttHeader.UPDATE_USER_STATUS;
        User user = new User();
        user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        user.setStatus(status);
        if (user.getUser_id() != -1) {
            mqttHelper.connectPublishSubscribe(getApplicationContext(), topic, header, user);
        }
    }
}
