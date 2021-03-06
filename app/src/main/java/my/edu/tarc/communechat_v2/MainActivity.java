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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.Menu;
import android.view.MenuItem;

import my.edu.tarc.communechat_v2.Fragment.ChatFragment;
import my.edu.tarc.communechat_v2.Fragment.FindFriendFragment;
import my.edu.tarc.communechat_v2.Fragment.FriendListFragment;
import my.edu.tarc.communechat_v2.Fragment.ProfileFragment;
import my.edu.tarc.communechat_v2.Utility.MyUtil;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper;
import my.edu.tarc.communechat_v2.model.User;

public class MainActivity extends AppCompatActivity {

    //don't change this
    //since a device can only be a client
    //so only a single MqttHelper object can exist at the same time
    public static final MqttHelper mqttHelper = new MqttHelper();

    private SharedPreferences pref;
    private BottomNavigationView bottomNavigationView;

    @Override
    //inflate top right menu bar items
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navi_top_menu_bar, menu);
        return true;
    }

    @Override
    //override method for top right menu bar
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_tarc_app:
                final String tarcAppName = "app.tarc.edu.my";
                intent = getPackageManager().getLaunchIntentForPackage(tarcAppName);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.error);
                    builder.setTitle(R.string.no_tarc_app_install_desc);
                    builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MyUtil.INSTANCE.openPlayStore(MainActivity.this, tarcAppName);
                        }
                    });
                    builder.setNegativeButton(R.string.not_now, null);
                    builder.show();
                }
                break;
            case R.id.nav_google_classroom:
                final String classroomAppName = "com.google.android.apps.classroom";
                intent = getPackageManager().getLaunchIntentForPackage(classroomAppName);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.error);
                    builder.setTitle(R.string.no_classroom_install_desc);
                    builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MyUtil.INSTANCE.openPlayStore(MainActivity.this, classroomAppName);
                        }
                    });
                    builder.setNegativeButton(R.string.not_now, null);
                    builder.show();
                }
                break;
            case R.id.nav_log_out:
                //clear shared preference then navigate user to login activity
                updateUserStatus("Offline");
                pref.edit().clear().apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.nav_edit_profile:
                startActivity(new Intent(MainActivity.this, UpdateProfileActivity.class));
                break;
            case R.id.nav_about_us:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                break;
            case R.id.nav_feedback:
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
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

        //check user login status
        //if not logged in, redirect user to login activity
        if (pref == null || pref.getInt(User.COL_USER_ID, -1) == -1) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        //check if user has GPS turn on
        //if not ask user if they want to turn on
        runLocationService();

        updateUserStatus("Online");

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        if (savedInstanceState == null) {
            //if no fragment has been inserted before
            //put a default fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment())
                    .commit();
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        initializeEncryption();
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
                return;
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 10, locationListener);
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
            //Log.d("[LocationService]", "Location changed, lgt: " + location.getLongitude() + " ltd: " + location.getLatitude());
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

    public void backupReminder() {
        AlertDialog.Builder reminder = new AlertDialog.Builder(MainActivity.this);
        reminder.setTitle(R.string.gps_not_found);
        reminder.setMessage(R.string.gps_not_found_desc1);
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


    private void initializeEncryption() {
        //listen to incoming secret key
        if (pref.getInt(User.COL_USER_ID, -1) != -1) {
            RoomSecretHelper.initializeRoomSecretHelper(getApplicationContext(), pref.getInt(User.COL_USER_ID, -1));
        }
    }
}
