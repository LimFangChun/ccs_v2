package my.edu.tarc.communechat_v2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import my.edu.tarc.communechat_v2.model.User;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 500;

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

        //check permission, compulsory
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        // Add a marker in user current location and center the camera
        User currentUser = new User();
        currentUser.setLast_longitude((double) pref.getFloat(User.COL_LAST_LONGITUDE, 0));
        currentUser.setLast_latitude((double) pref.getFloat(User.COL_LAST_LATITUDE, 0));
        LatLng user = new LatLng(currentUser.getLast_latitude(), currentUser.getLast_longitude());

        User targetUser = new User();
        targetUser.setLast_longitude(getIntent().getDoubleExtra(User.COL_LAST_LONGITUDE, 0));
        targetUser.setLast_latitude(getIntent().getDoubleExtra(User.COL_LAST_LATITUDE, 0));
        targetUser.setDisplay_name(getIntent().getStringExtra(User.COL_DISPLAY_NAME));
        LatLng target = new LatLng(targetUser.getLast_latitude(), targetUser.getLast_longitude());

        Marker mUser = mMap.addMarker(new MarkerOptions().position(user).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mUser.setTag(0);
        Marker mTarget = mMap.addMarker(new MarkerOptions().position(target).title(targetUser.getDisplay_name()));
        mTarget.setTag(0);
        //Toast.makeText(this, targetUser.getLast_longitude() + ", " + targetUser.getLast_latitude(), Toast.LENGTH_LONG).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user.latitude, user.longitude), 10));

        //get direction between 2 points, using Google API
        //source: https://www.youtube.com/watch?v=jg1urt3FGCY
        String url = getRequestUrl(user, target);
        TaskRequestDirections taskRequestDirection = new TaskRequestDirections();
        taskRequestDirection.execute(url);
    }

    //https://www.youtube.com/watch?v=jg1urt3FGCY
    private String getRequestUrl(LatLng user, LatLng target) {
        //Value of origin
        String str_org = "origin=" + user.latitude + "," + user.longitude;
        //Value of destination
        String str_dest = "destination=" + target.latitude + "," + target.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //API key
        String key = "key=" + getString(R.string.api_key);
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;
        //Output format
        String output = "json";

        //Create url to request
        //Log.d("Direction", "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param);
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
    }

    //source: https://www.youtube.com/watch?v=jg1urt3FGCY
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            responseString = stringBuilder.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    //source: https://www.youtube.com/watch?v=jg1urt3FGCY
    @SuppressLint("StaticFieldLeak")
    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            //Log.d("Direction", s);
            taskParser.execute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
