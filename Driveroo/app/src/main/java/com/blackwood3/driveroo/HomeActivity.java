package com.blackwood3.driveroo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Date dt=new Date();
        String hour=dt.toString().split(" ")[3].split(":")[0];
        int hourInt=Integer.parseInt(hour);
        TextView home_greeting= (TextView)findViewById(R.id.home_Greeting);
        final String username=intent.getStringExtra(Intent.EXTRA_TEXT);

        if (hourInt>=4 && hourInt<12) home_greeting.setText("Good morning, "+username+"!");
        else {
            if (hourInt >= 12 && hourInt < 18)
                home_greeting.setText("Good afternoon, " + username + "!");
            else {

                home_greeting.setText("Good evening, " + username + "!");
            }
        }


        ImageButton profiBtn= (ImageButton) findViewById(R.id.imageButton2);
        profiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent=new Intent(getApplicationContext(),ProfileActivity.class);
                profileIntent.putExtra(Intent.EXTRA_TEXT, username);
                startActivity(profileIntent);
            }
        });

        Button startBtn2=(Button)findViewById(R.id.home_button);
        startBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent2=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent2);
            }
        });

//        ImageButton startBtn=(ImageButton) findViewById(startBtn);
//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent startIntent=new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(startIntent);
//            }
//        });

    }
}
