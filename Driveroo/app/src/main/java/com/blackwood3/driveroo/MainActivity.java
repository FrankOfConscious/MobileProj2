package com.blackwood3.driveroo;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    private Location startLocation;

    private Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE = 99;

    double start_latitude, start_longitude;
    double end_latitude, end_longitude;
    private ArrayList<LatLng> points;
    Boolean ifCheckServer = true;
    Polyline line;

    LocationManager locationManager;
    //TextView timeTv;
    TextView disTv;
    ImageView startBtn2;
    ImageView endBtn;
    Chronometer chronometer;

    Boolean isRunning;
    long lastPause;
    long recordTime;
    static String recordTimeStr;

    private static LatLng previousLatlng;
    private static float totalDistance = 0;
    private static String distanceText;
    MediaPlayer mplay1;
    MediaPlayer mplay2;
    Vibrator mVib;


    // variables below are used for chronometer.
    Context mainContext;

    private static final long INTERVAL_OND_SECOND = 1000;

    private String decimalPlaces = "80";
    private int colors[] ={Color.parseColor("#86FF59"),Color.parseColor("#FF5C1C")};//86FF59 //#ffe476
    private EditText editText;
    private ConstraintLayout rl;
    private TextView stateText;
    private int currentWarningLevel;
    private long currentTime;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            int warning=msg.what;
            int editLength=editText.getText().length();
            String level=editText.getText().toString();
            int offset=warning*10-editLength;
            if(offset==0){
                if(warning>=3){
                    //currentTime=System.currentTimeMillis();
                    if(currentTime+5000<System.currentTimeMillis()){
                        if(warning>=6){
                            Toast.makeText(MainActivity.this, "Warning >= 6", Toast.LENGTH_SHORT).show();
                            mplay2.start();
                            currentTime=System.currentTimeMillis();
                            mVib.vibrate(1000);
                        }else{
                            Toast.makeText(MainActivity.this, "Warning < 6", Toast.LENGTH_SHORT).show();
                            mplay1.start();
                            currentTime=System.currentTimeMillis();
                            mVib.vibrate(1000);
                        }
                    }
                }
            }
            if(offset>0){
                if(warning>=3){
                    //currentTime=System.currentTimeMillis();
                    if(currentTime+5000<System.currentTimeMillis()){
                        if(warning>=6){
                            Toast.makeText(MainActivity.this, "Warning >= 6", Toast.LENGTH_SHORT).show();
                            mplay2.start();
                            currentTime=System.currentTimeMillis();
                            mVib.vibrate(1000);
                        }else{
                            Toast.makeText(MainActivity.this, "Warning < 6", Toast.LENGTH_SHORT).show();
                            mplay1.start();
                            currentTime=System.currentTimeMillis();
                            mVib.vibrate(1000);
                        }
                    }
                }
                for(int i=0; i<offset;i++){
                    level+="w";
                    if(editText.getText().length()>80) break;
                }
                 editText.setText(level);
            }
            if(offset<0){
                if(editText.getText().length()+offset <=0) level="";
                else level=level.substring(0,(editText.getText().length()+offset));
                editText.setText(level);
            }
        }
    };


    // start of the main function

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=(EditText)findViewById(R.id.level);
        rl=(ConstraintLayout) findViewById(R.id.main_layout);
        rl.setBackgroundColor(colors[0]);
        mplay1=MediaPlayer.create(this,R.raw.warn1);
        mplay2=MediaPlayer.create(this,R.raw.warn2);
        mVib=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        currentTime=System.currentTimeMillis();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int editSize = editText.getText().length();

                if (editSize > Integer.valueOf(decimalPlaces))return;

                BigDecimal bigEs= BigDecimal.valueOf(editSize);
                BigDecimal result = bigEs.divide(new BigDecimal(decimalPlaces), 8, RoundingMode.HALF_UP);

                ArgbEvaluator evaluator = new ArgbEvaluator();

                int evaluate = (int) evaluator.evaluate(result.floatValue(),colors[0],colors[1]);
                rl.setBackgroundColor(evaluate);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        points = new ArrayList<LatLng>(); // initialize points

        mainContext = this;

        //timeTv = (TextView) findViewById(R.id.timeTv);
        disTv = (TextView) findViewById(R.id.disTv);

        startBtn2 = (ImageView) findViewById(R.id.startBtn2);
        endBtn = (ImageView) findViewById(R.id.endBtn);

        chronometer = (Chronometer) findViewById(R.id.chronometer);

        isRunning = false;

        startBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // add something >

                // add something <

                if (!isRunning) {
                    // chronometer is not running, use following code to start or resume.
                    if (lastPause == 0) {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                    } else {
                        chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                    }
                    chronometer.start();
                    isRunning = true;
                    startBtn2.setImageResource(R.drawable.pausefinal2_meitu_6);
                    ifCheckServer = true;

                    new Thread(runnable_start).start();
                    new Thread(runnable_warning).start();
                } else {
                    // chronometer is running, and use following code to stop.
                    lastPause = SystemClock.elapsedRealtime();
                    chronometer.stop();
                    isRunning = false;
                    startBtn2.setImageResource(R.drawable.startfinal2_meitu_4);
                    ifCheckServer = false;
                    editText.setText("");
                    new Thread(runnable_end).start();
                }
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!isRunning) {
//                    recordTime = lastPause - chronometer.getBase();
//                    recordTimeStr = String.valueOf(recordTime);
//                    Toast.makeText(MainActivity.this, recordTimeStr, Toast.LENGTH_SHORT).show();
//                } else {
//                    recordTime = SystemClock.elapsedRealtime() - chronometer.getBase();
//                    recordTimeStr = String.valueOf(recordTime);
//                    Toast.makeText(MainActivity.this, recordTimeStr, Toast.LENGTH_SHORT).show();
//                }
//                if(isRunning){
//                    ifCheckServer = false;
//                    new Thread(runnable_end).start();
//                    recordTime = SystemClock.elapsedRealtime() - chronometer.getBase();
//                    if (distanceText == null) {
//                        distanceText = "0.0 m";
//                    }
//                    String timeStr = timeDifference(recordTime);
//
//                    Intent userintent = getIntent();
//                    String username = userintent.getStringExtra(Intent.EXTRA_TEXT);
//                    Intent endIntent = new Intent(getApplicationContext(), EndActivity.class);
//                    endIntent.putExtra("time", timeStr);
//                    endIntent.putExtra("distance", distanceText);
//                    endIntent.putExtra("username", username);
//                    startActivity(endIntent);
//                    finish();
//                }else{
//                    Toast.makeText(MainActivity.this, "You have not started driving!", Toast.LENGTH_SHORT).show();
//                }
                if(isRunning){
                    ifCheckServer = false;
                    new Thread(runnable_end).start();
                    recordTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                    if (distanceText == null) {
                        distanceText = "0.0 m";
                    }
                    String timeStr = timeDifference(recordTime);

                    Intent userintent = getIntent();
                    String username = userintent.getStringExtra(Intent.EXTRA_TEXT);
                    Intent endIntent = new Intent(getApplicationContext(), EndActivity.class);
                    endIntent.putExtra("time", timeStr);
                    endIntent.putExtra("distance", distanceText);
                    endIntent.putExtra("username", username);
                    startActivity(endIntent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "You have not started driving!", Toast.LENGTH_SHORT).show();
                }
            }
        });
       //*************************************

   }


    Runnable runnable_warning = new Thread(new Runnable() {

        public void run() {
            Intent intent = getIntent();
            JSONObject get_result = null;
            String username = intent.getStringExtra(Intent.EXTRA_TEXT);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            int warningLevel=0;

            while(true){
                try {
                    if(ifCheckServer){
                        get_result = HttpUtils.submitGETData(params, "utf-8", "check_server");
                        try {
                            String info = get_result.getString("info");
                            Log.w("Info",info);
                            JSONObject result = new JSONObject(info);
                            final Boolean warning = Boolean.valueOf(result.getString("ifWarning"));
                            final Boolean recovery = Boolean.valueOf(result.getString("ifRecovery"));
                            final Boolean ifStart = Boolean.valueOf(result.getString("ifStart"));
                            final Boolean isNoWarning=!(warning ||recovery);
                            if(warning) warningLevel=Math.min(warningLevel+1,8);
                            if(recovery) warningLevel=Math.max(0,warningLevel-1);
                            if(isNoWarning) warningLevel=Math.max(0,warningLevel-1);
                            Message msg= new Message();
                            msg.what=warningLevel;
                            handler.sendMessage(msg);
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        Log.w("Thread interrupt","True");
                        Thread.interrupted();
                        break;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }



        }
    }
    );
    Runnable runnable_start = new Thread(new Runnable() {
        @Override
        public void run() {
            Intent intent = getIntent();
            String username = intent.getStringExtra(Intent.EXTRA_TEXT);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            try{
                HttpUtils.submitGETData(params, "utf-8", "update_start");
            }catch (MalformedURLException e){
                e.printStackTrace();
            }

        }
    });

    Runnable runnable_end = new Thread(new Runnable() {
        @Override
        public void run() {
            Intent intent = getIntent();
            JSONObject get_result = null;
            String username = intent.getStringExtra(Intent.EXTRA_TEXT);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            try{
                HttpUtils.submitGETData(params, "utf-8", "update_end");
            }catch (MalformedURLException e){
                e.printStackTrace();
            }

        }
    });



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission is granted
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    // show message
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
                return;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           buildGoogleApiClient();
           mMap.setMyLocationEnabled(true);
       }

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + start_latitude + "," + start_longitude);
        googleDirectionsUrl.append("&destination=" + end_latitude + "," + end_longitude);
        googleDirectionsUrl.append("&key=" + "AIzaSyA-b00JQ3i0Bv6wSzRY1H_zRKAEFVX4ztY");

        return googleDirectionsUrl.toString();
    }


    // check location permission
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_CODE);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }


    // auto added when added extra interfaces


    // This function is used to track users location and draw polyline on map
    @Override
    public void onLocationChanged(Location location) {

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        if (startLocation == null) {
            startLocation = location;
            start_latitude = startLocation.getLatitude();
            start_longitude = startLocation.getLongitude();
        }

        if (previousLatlng == null) {
            previousLatlng = new LatLng(start_latitude, start_longitude);
        }

        end_latitude = location.getLatitude();
        end_longitude = location.getLongitude();

        // show location on toast
        String locationStr = String.valueOf(end_latitude);
        locationStr += ", ";
        locationStr += String.valueOf(end_longitude);

        // show changed location
        LatLng latLng = new LatLng(end_latitude, end_longitude);

        // show distance
        float[] result = new float[1];
        Location.distanceBetween(previousLatlng.latitude, previousLatlng.longitude, end_latitude, end_longitude, result);
        totalDistance += result[0];
        float showDistance = totalDistance;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        if (showDistance >= 1000) {
            showDistance = showDistance/1000;
            distanceText = "";
            distanceText += decimalFormat.format(showDistance);
            distanceText += " km";
        } else {
            distanceText = "";
            distanceText += decimalFormat.format(showDistance);
            distanceText += " m";
        }

        disTv.setText(distanceText);
        previousLatlng = latLng;

        // set markers on map
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Start location");
////        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.black_car_icon));
//        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.1f));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(16.2f));

//        Toast.makeText(MainActivity.this, "Now At: " + locationStr, Toast.LENGTH_SHORT).show();
//        Toast.makeText(MainActivity.this, distanceText, Toast.LENGTH_SHORT).show();
        previousLatlng = latLng;
        points.add(latLng);
        redRawLine(mMap, points, latLng, line);

        // using directions API
//        String url = getDirectionsUrl();
//        Object dataTransfer[] = new Object[3];
//        GetDirectionsData getDirectionsData = new GetDirectionsData();
//        dataTransfer[0] = mMap;
//        dataTransfer[1] = url;
//        dataTransfer[2] = new LatLng(end_latitude, end_longitude);
//        getDirectionsData.execute(dataTransfer);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }


    // This method is used to draw polyline on map fragment
    private static void redRawLine(GoogleMap mMap, ArrayList<LatLng> points, LatLng currentLocation, Polyline line) {
        mMap.clear(); // clear all markers and Polylines
        PolylineOptions options = new PolylineOptions().width(10).color(Color.CYAN).geodesic(true);

        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }

//        mMap.addMarker(new MarkerOptions().position(currentLocation)); // add marker at current position
        line = mMap.addPolyline(options);
    }

    public static String timeDifference(long timeDifference1) {
        long timeDifference = timeDifference1 / 1000;
        int h = (int) (timeDifference / (3600));
        int m = (int) ((timeDifference - (h * 3600)) / 60);
        int s = (int) (timeDifference - (h * 3600) - m * 60);

        return String.format("%02d:%02d:%02d", h, m, s);
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
