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

import my.edu.tarc.communechat_v2.Fragment.ChatFragment;
import my.edu.tarc.communechat_v2.Fragment.FindFriendFragment;
import my.edu.tarc.communechat_v2.Fragment.FriendListFragment;
import my.edu.tarc.communechat_v2.Fragment.ProfileFragment;
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
                Intent intent = new Intent(this, SettingsActivity.class );
                startActivity(intent);
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
        if (pref == null || pref.getInt(User.COL_USER_ID, -1) == -1) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void runLocationService() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    locationManager != null) {
                String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permission, 112);
            } else if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                pref.edit().putLong(User.COL_LAST_LONGITUDE, (long) location.getLongitude()).apply();
                pref.edit().putLong(User.COL_LAST_LATITUDE, (long) location.getLatitude()).apply();
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

        updateUserStatus("Offline");
        mqttHelper.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus("Online");

        runLocationService();
    }

    private void updateUserStatus(String status) {
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
            pref.edit().putLong(User.COL_LAST_LONGITUDE, (long) location.getLongitude()).apply();
            pref.edit().putLong(User.COL_LAST_LATITUDE, (long) location.getLatitude()).apply();
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
        String topic = "updateLocation/" + pref.getInt(User.COL_USER_ID, -1);
        String header = MqttHeader.UPDATE_LOCATION;
        User user = new User();
        user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        user.setLast_longitude(longitude);
        user.setLast_latitude(latitude);

        mqttHelper.connectPublish(this, topic, header, user);
    }
}
