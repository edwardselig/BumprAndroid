package com.bumpr.bumpr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //private HashMap hMap;

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
        mMap.setMyLocationEnabled(true);
        final HashMap<Marker,String> hMap = new HashMap<Marker,String>();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }else {
            LocationListener listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    LatLng location1 = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    Log.d("myTag", "This is my message" + longitude + " " + latitude);
                    Context context = MapsActivity.this;
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String id = preferences.getString("id","");
                    String urlString ="http://www.socialgainz.com/Bumpr/location.php?id=" + id + "&latitude=" + latitude + "&longitude=" + longitude;
                    //try {
                    //String urlString1 = URLEncoder.encode(urlString, "UTF-8");
                    final String urlString1 = urlString.replaceAll(" ", "%20");
                    Log.d("myTag", "Response:" + urlString1);
                    //}catch(UnsupportedEncodingException e) {
                    //  e.printStackTrace();
                    //}
                    new Thread(new Runnable() {
                        public void run() {
                            try{
                                URL url = new URL(urlString1);
                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                String resp = readStream(in);
                                Log.d("myTag", "Response:" + resp);
                                JSONArray array = new JSONArray(resp);
                                for(int i = 0; i< array.length(); i++){
                                    JSONObject obj = array.getJSONObject(i);
                                    final String first = obj.getString("firstname");
                                    final String bio = obj.getString("bio");
                                    final String ID = obj.getString("id");
                                    double latitude = Double.parseDouble(obj.getString("Latitude"));
                                    double longitude = Double.parseDouble(obj.getString("Longitude"));
                                    final LatLng location = new LatLng(latitude, longitude);
                                    MapsActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mMap.clear();
                                            hMap.put(mMap.addMarker(new MarkerOptions().position(location).title(first)),ID);
                                        }
                                    });
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                    // TODO Auto-generated method stub
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, listener);
        }
        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(MapsActivity.this, profileClick.class);
                i.putExtra("id",hMap.get(marker));
                startActivity(i);
                Log.d("myTag", hMap.get(marker));
            }
        });
        //Location myLocation = locationManager.getLastKnownLocation(provider);
            /*Runnable runnable = new Runnable() {
                public void run() {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                    }else {
                        Handler handler = new Handler();
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        String provider = locationManager.getBestProvider(criteria, true);
                        Location myLocation = locationManager.getLastKnownLocation(provider);
                        double longitude = myLocation.getLongitude();
                        double latitude = myLocation.getLatitude();
                        LatLng sydney = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        Log.d("myTag", "This is my message" + longitude + ' ' + latitude);
                        handler.postDelayed(this, 5000);
                    }
                }
            };
            runnable.run();
        // Add a marker in Sydney and move the camera*/
    }
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
