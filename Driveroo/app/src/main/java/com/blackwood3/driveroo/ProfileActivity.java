package com.blackwood3.driveroo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        new Thread(runnable).start();

    }

    Runnable runnable = new Thread(new Runnable() {

        public void run() {
            Intent intent = getIntent();
            JSONObject get_result = null;
            String username = intent.getStringExtra(Intent.EXTRA_TEXT);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            try {
                get_result = HttpUtils.submitGETData(params, "utf-8", "getProfile");
                try {
                    JSONObject profile = new JSONObject(get_result.getString("profile"));
                    String Smobile = profile.getString("mobile");
                    String Scar = profile.getString("car");
                    String Semail = profile.getString("email");
                    TextView name = (TextView) findViewById(R.id.profile_name);
                    TextView dob = (TextView) findViewById(R.id.profile_DOB);
                    TextView email = (TextView) findViewById(R.id.profile_Email);
                    name.setText("Name: "+username);
                    dob.setText("Car: "+Scar);
                    email.setText("Email: "+Semail);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }
    );
}
