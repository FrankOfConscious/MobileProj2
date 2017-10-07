package com.blackwood3.driveroo;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;

/**
 * Created by liam on 4/10/17.
 */

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String url;
    String directionsData;
    String duration, distance;
    LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        DownloadUrl downloadUrl = new DownloadUrl();

        try {
            directionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return directionsData;
    }

    @Override
    protected void onPostExecute(String s) {

//        HashMap<String, String> directionList = null;
//        DataParser parser= new DataParser();
//
//        directionList = parser.parseDirections(s);
//        duration = directionList.get("duration");
//        distance = directionList.get("distance");
//
//        mMap.clear();
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.draggable(true);
//        markerOptions.title("Duration = " + duration);
//        markerOptions.snippet("Distance = " + distance);
//
//        mMap.addMarker(markerOptions);

        String[] directionsList;
        DataParser dataParser = new DataParser();
        directionsList = dataParser.parseDirections(s);
        displayDirection(directionsList);
    }

    public void displayDirection(String[] directionList) {
        int count = directionList.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(directionList[i]));

            mMap.addPolyline(options);
        }
    }

}
