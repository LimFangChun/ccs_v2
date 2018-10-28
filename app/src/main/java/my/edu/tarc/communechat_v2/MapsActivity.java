package my.edu.tarc.communechat_v2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import my.edu.tarc.communechat_v2.model.User;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        // Add a marker in user current location and center the camera
        User currentUser = new User();
        currentUser.setLast_longitude((double) pref.getFloat(User.COL_LAST_LONGITUDE, 0));
        currentUser.setLast_latitude((double) pref.getFloat(User.COL_LAST_LATITUDE, 0));
        LatLng user = new LatLng(currentUser.getLast_longitude(), currentUser.getLast_latitude());

        User targetUser = new User();
        targetUser.setLast_longitude(getIntent().getDoubleExtra(User.COL_LAST_LONGITUDE, 0));
        targetUser.setLast_latitude(getIntent().getDoubleExtra(User.COL_LAST_LATITUDE, 0));
        targetUser.setDisplay_name(getIntent().getStringExtra(User.COL_DISPLAY_NAME));
        LatLng target = new LatLng(targetUser.getLast_longitude(), targetUser.getLast_latitude());

        Marker mUser = mMap.addMarker(new MarkerOptions().position(user).title("You"));
        mUser.setTag(0);
        Marker mTarget = mMap.addMarker(new MarkerOptions().position(target).title(targetUser.getDisplay_name()));
        mTarget.setTag(0);
        Toast.makeText(this, targetUser.getLast_longitude() + ", " + targetUser.getLast_latitude(), Toast.LENGTH_LONG).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
    }
}
