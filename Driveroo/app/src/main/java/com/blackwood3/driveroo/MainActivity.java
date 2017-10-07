package com.blackwood3.driveroo;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import android.content.Intent;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private Handler handler;
    LocationManager locationManager;

    TextView timeTv;
    TextView disTv;
    ImageView startBtn2;
    ImageView endBtn;

    Chronometer mChronometer;
    Thread mChronoThread;
    Context mainContext;

    Boolean chronoIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mainContext = this;

        timeTv = (TextView) findViewById(R.id.timeTv);
        disTv = (TextView) findViewById(R.id.disTv);

        startBtn2 = (ImageView) findViewById(R.id.startBtn2);
        endBtn = (ImageView) findViewById(R.id.endBtn);
        TextView testString=(TextView) findViewById(R.id.testString);
        testString.setText("put String in this place and it will show on screen");
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
                        startBtn2.setImageResource(R.drawable.pausefinal2_meitu_6);
                    }

                } else {

                    if (mChronometer != null) {
                        mChronometer.stop();
                        mChronoThread.interrupt();
                        mChronoThread = null;
                        mChronometer = null;

                        chronoIsRunning = false;
                        startBtn2.setImageResource(R.drawable.startfinal2_meitu_4);
                    }
                }


            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

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
        new Thread(runnable).start();
   }



    Runnable runnable = new Thread(new Runnable() {

        public void run() {
            Intent intent = getIntent();
            JSONObject get_result = null;
            String username = intent.getStringExtra(Intent.EXTRA_TEXT);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);

            while(true){
                try {

                    get_result = HttpUtils.submitGETData(params, "utf-8", "check_server");
                    try {
                        String info = get_result.getString("info");
                        Log.w("Info",info);
                        JSONObject result = new JSONObject(info);
                        final Boolean warning = Boolean.valueOf(result.getString("ifWarning"));
                        final Boolean recovery = Boolean.valueOf(result.getString("ifRecovery"));
                        try{

                            Thread.sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(warning && !recovery){
                                        TextView testString=(TextView) findViewById(R.id.testString);
                                        testString.setText("Warning");
                                    }else if(!warning && recovery){
                                        TextView testString=(TextView) findViewById(R.id.testString);
                                        testString.setText("Recovery");
                                    }else{
                                        TextView testString=(TextView) findViewById(R.id.testString);
                                        testString.setText("No Warning");
                                    }
                                }
                            });
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    );
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
}
