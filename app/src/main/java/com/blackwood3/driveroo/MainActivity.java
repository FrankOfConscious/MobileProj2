package com.blackwood3.driveroo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private ArrayList<LatLng> points;
    Polyline line;

    LocationManager locationManager;
    TextView timeTv;
    TextView disTv;
    Button startBtn2;
    Button endBtn;

    private static LatLng previousLatlng;
    private static float totalDistance = 0;


    // variables below are used for chronometer.
    Chronometer mChronometer;
    Thread mChronoThread;
    Context mainContext;
    Boolean chronoIsRunning;

    private static final long INTERVAL_OND_SECOND = 1000;

//    // variable below are used for setting minimum distance between 2 points where polyline will be
//    // regenerated.
//
//    private static final String TAG = "MainActivity";
//    private static final long INTERVAL = 1000 * 60 * 1; // 1min
//    private static final long FASTES_INTERVAL = 1000 * 60 * 1; // 1min
//    private static final float SMALLEST_DISPLACEMENT = 0.25F;



    // start of the main function
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        points = new ArrayList<LatLng>(); // initialize points

        mainContext = this;

        timeTv = (TextView) findViewById(R.id.timeTv);
        disTv = (TextView) findViewById(R.id.disTv);

        startBtn2 = (Button) findViewById(R.id.startBtn2);
        endBtn = (Button) findViewById(R.id.endBtn);
        chronoIsRunning = false;

        startBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chronoIsRunning) {

                    if (mChronometer == null) {
                        mChronometer = new Chronometer(mainContext);
                        mChronoThread = new Thread(mChronometer);
                        mChronoThread.start();
                        mChronometer.start();

                        chronoIsRunning = true;
                        startBtn2.setText("Pause");
                    }

                } else {

                    if (mChronometer != null) {
                        mChronometer.stop();
                        mChronoThread.interrupt();
                        mChronoThread = null;
                        mChronometer = null;

                        chronoIsRunning = false;
                        startBtn2.setText("Start");
                    }
                }


            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL_OND_SECOND, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

                    points.add(latLng);

                    redRawLine(mMap, points, latLng, line);

                    if (previousLatlng == null) {

                        previousLatlng = latLng;
                    } else {

                        String distanceText = "Distance: ";
                        float[] resultDistance = new float[1];

                        Location.distanceBetween(previousLatlng.latitude, previousLatlng.longitude,
                                latLng.latitude, latLng.longitude, resultDistance);
                        float tempDistance = resultDistance[0];
                        totalDistance += tempDistance;

                        distanceText += String.valueOf(totalDistance);
                        distanceText += " m";
                        disTv.setText(distanceText);

                        previousLatlng = latLng;
                    }

                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality() + ", ";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.1f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
            });

        }

        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_OND_SECOND, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

                    points.add(latLng);

                    redRawLine(mMap, points, latLng, line);

                    if (previousLatlng == null) {

                        previousLatlng = latLng;
                    } else {

                        String distanceText = "Distance: ";
                        float[] resultDistance = new float[1];

                        Location.distanceBetween(previousLatlng.latitude, previousLatlng.longitude,
                                latLng.latitude, latLng.longitude, resultDistance);
                        float tempDistance = resultDistance[0];
                        totalDistance += tempDistance;

                        distanceText += String.valueOf(totalDistance);
                        distanceText += " m";
                        disTv.setText(distanceText);

                        previousLatlng = latLng;
                    }

                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality() + ", ";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.1f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            });
        }
   }

    public void updateTimeText(final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "Time: ";
                text += time;
                timeTv.setText(text);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.1f));
    }


    private static void redRawLine(GoogleMap mMap, ArrayList<LatLng> points, LatLng currentLocation, Polyline line) {
        mMap.clear(); // clear all markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }

//        mMap.addMarker(new MarkerOptions().position(currentLocation)); // add marker at current position
        line = mMap.addPolyline(options);
    }


//    private void redRawLine() {
//        mMap.clear(); // clear all markers and Polylines
//        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//
//        for (int i = 0; i < points.size(); i++) {
//            LatLng point = points.get(i);
//            options.add(point);
//        }
//
//        mMap.addMarker(new MarkerOptions().position(latLng)); // add marker at current position
//        line = mMap.addPolyline(options);
//    }
}
