package com.blackwood3.driveroo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liam on 4/10/17.
 */

public class DataParser {

    private HashMap<String, String> getDuration(JSONArray directionsJson) {
        HashMap<String, String> directionsMap = new HashMap<>();
        String duration = "";
        String distance = "";

        Log.d("JSON response", directionsJson.toString());


        try {
            duration = directionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = directionsJson.getJSONObject(0).getJSONObject("distance").getString("text");

            directionsMap.put("duration", duration);
            directionsMap.put("distance", distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return directionsMap;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJSON) {

        HashMap<String, String> googlePlacesMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
            if (!googlePlaceJSON.isNull("name")) {
                placeName = googlePlaceJSON.getString("name");
            }

            if (!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }

            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");

            googlePlacesMap.put("placeName", placeName);
            googlePlacesMap.put("vicinity", vicinity);
            googlePlacesMap.put("lat", latitude);
            googlePlacesMap.put("lng", longitude);
            googlePlacesMap.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlacesMap;
    }


    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {

        int count = jsonArray.length();
        List<HashMap<String, String>> placeList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for (int i = 0; i < count; i++) {
            try {
                placeMap = getPlace ((JSONObject) jsonArray.get(i));
                placeList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placeList;
    }

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }



    public String[] parseDirections(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps"); // legs array
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        return getDuration(jsonArray);
        return getPaths(jsonArray);
    }

    public String getPath(JSONObject googlePathJson) {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return polyline;
    }

    public String[] getPaths(JSONArray googleStepsJson) {
        int count = googleStepsJson.length();

        String[] polylines = new String[count];

        for (int i = 0; i < count; i++) {
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;
    }
}
