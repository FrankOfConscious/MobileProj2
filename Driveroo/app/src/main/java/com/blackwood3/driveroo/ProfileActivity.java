package com.blackwood3.driveroo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        new Thread(runnable).start();
        TextView logOut=(TextView) findViewById(R.id.logout);

        /**
         * Set a Click Listener on TextView "logOut", press this will logout current account
         *and jump to login page. After that, this activity will be finished.
         */
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jumpToLogin=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(jumpToLogin);
                finish();
            }
        });

    }

    /**
     * After the profile activity starts, the client app will send request to server to get
     * user's information like name, mobile number, email, and display on this page.
     */
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
                    String car_number = profile.getString("car");
                    String Semail = profile.getString("email");
                    TextView name = (TextView) findViewById(R.id.profile_name);
                    TextView email = (TextView) findViewById(R.id.profile_Email);
                    TextView car = (TextView) findViewById(R.id.profile_Car);
                    TextView mobile = (TextView) findViewById(R.id.profile_record);
                    name.setText("Name   :"+username);
                    email.setText("Email   :"+Semail);
                    car.setText("Car       :"+car_number);
                    mobile.setText("Mobile :"+Smobile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    });
}
