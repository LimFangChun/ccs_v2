package my.edu.tarc.communechat_v2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import my.edu.tarc.communechat_v2.Fragment.FindFriendFragment;
import my.edu.tarc.communechat_v2.Fragment.FriendListFragment;
import my.edu.tarc.communechat_v2.Fragment.ProfileFragment;
import my.edu.tarc.communechat_v2.chatEngine.ChatFragment;
import my.edu.tarc.communechat_v2.chatEngine.SelectContactActivity;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.User;

public class MainActivity extends AppCompatActivity {

    //don't change this
    //since a device can only be a client
    //so only a single MqttHelper object can exist at the same time
    public static final MqttHelper mqttHelper = new MqttHelper();

    private SharedPreferences pref;
    private BottomNavigationView bottomNavigationView;
    private MenuItem mAddPrivateChatRoom, mAddGroupChatRoom;
    @Override
    //inflate top right menu bar items
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navi_top_menu_bar, menu);
        mAddGroupChatRoom = menu.getItem(0);
        mAddPrivateChatRoom = menu.getItem(1);
        return true;
    }

    @Override
    //override method for top right menu bar
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.nav_settings:

                //new ChatEngineStartup(this).execute();
                //MainActivity.mqttHelper.subscribe(my.edu.tarc.communechat_v2.chatEngine.ChatFragment.CURRENT_USER_ID +"");
               // MainActivity.mqttHelper.getMqttClient().setCallback(new ChatSubscribeCallBack(this));
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_log_out:
                //clear shared preference then navigate user to login activity
                updateUserStatus("Offline");
                pref.edit().clear().apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.nav_add_private_chat:
                //TODO: CE
                Intent intent = new Intent(this, SelectContactActivity.class);
                intent.putExtra(SelectContactActivity.SELECTION_TYPE, SelectContactActivity.SELECT_PRIVATE_CHAT_MEMBER);
                startActivity(intent);
                break;
            case R.id.nav_add_group_chat:
                Intent intent2 = new Intent(this, SelectContactActivity.class);
                intent2.putExtra(SelectContactActivity.SELECTION_TYPE, SelectContactActivity.SELECT_GROUP_CHAT_MEMBER);
                startActivity(intent2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_bottom);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //check if user has GPS turn on
        //if not ask user if they want to turn on
        runLocationService();

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        if (savedInstanceState == null) {
            //if no fragment has been inserted before
            //put a default fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment())
                    .commit();
        }

        //check user login status
        //if not logged in, redirect user to login activity
//        if (pref == null || pref.getInt(User.COL_USER_ID, -1) == -1) {
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        }
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    //method to get user's current longitude and latitude
    private void runLocationService() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //check if user has enable GPS
            if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //if GPS not enabled, notice user turning on GPS has impact in using the app
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle(R.string.gps_not_found);
                alertDialog.setMessage(R.string.gps_not_found_desc1);
                alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                alertDialog.setNegativeButton(R.string.no, null);
                alertDialog.create().show();
            }

            //check if user has granted permission to the app to access to GPS service
            //if no request permission from user
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    locationManager != null) {
                String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permission, 112);
            } else if (locationManager != null) {
                //else get user's current longitude and latitude
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                pref.edit().putFloat(User.COL_LAST_LONGITUDE, (float) location.getLongitude()).apply();
                pref.edit().putFloat(User.COL_LAST_LATITUDE, (float) location.getLatitude()).apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    //TODO:CE

                    mAddGroupChatRoom.setVisible(false);
                    mAddPrivateChatRoom.setVisible(false);
                    switch (item.getItemId()) {
                        case R.id.nav_chat:
                            //TODO: CE
                            mAddGroupChatRoom.setVisible(true);
                            mAddPrivateChatRoom.setVisible(true);
                            selectedFragment = new my.edu.tarc.communechat_v2.chatEngine.ChatFragment();
                            break;
                        case R.id.nav_find_friend:
                            selectedFragment = new FindFriendFragment();
                            break;
                        case R.id.nav_add_friend:
                            selectedFragment = new FriendListFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //update user status
        updateUserStatus("Offline");

        //disconnect mqtt helper
        mqttHelper.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateUserStatus(String status) {
        if (pref.getInt(User.COL_USER_ID, -1) == -1) {
            return;
        }

        String topic = "updateUserStatus/" + pref.getInt(User.COL_USER_ID, -1);
        String header = MqttHeader.UPDATE_USER_STATUS;
        User user = new User();
        user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        user.setStatus(status);
        if (user.getUser_id() != -1) {
            mqttHelper.connectPublish(getApplicationContext(), topic, header, user);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (pref.getInt(User.COL_USER_ID, -1) == -1) {
                return;
            }

            pref.edit().putFloat(User.COL_LAST_LONGITUDE, (float) location.getLongitude()).apply();
            pref.edit().putFloat(User.COL_LAST_LATITUDE, (float) location.getLatitude()).apply();
            Log.d("[LocationService]", "Location changed, lgt: " + location.getLongitude() + " ltd: " + location.getLatitude());
            updateUserLocation(location.getLongitude(), location.getLatitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void updateUserLocation(double longitude, double latitude) {
        if (pref.getInt(User.COL_USER_ID, -1) == -1) {
            return;
        }

        String topic = "updateLocation/" + pref.getInt(User.COL_USER_ID, -1);
        String header = MqttHeader.UPDATE_LOCATION;
        User user = new User();
        user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        user.setLast_longitude(longitude);
        user.setLast_latitude(latitude);

        mqttHelper.connectPublish(this, topic, header, user);
    }

    public void backupReminder(){
        AlertDialog.Builder reminder = new AlertDialog.Builder(MainActivity.this);
        reminder.setTitle(R.string.gps_not_found);
        reminder.setMessage(R.string.gps_not_found_desc1);
    }
}
