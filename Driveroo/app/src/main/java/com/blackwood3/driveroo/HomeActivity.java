package com.blackwood3.driveroo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
        TextView home_greeting2=(TextView)findViewById(R.id.home_greeting2);
        final String username=intent.getStringExtra(Intent.EXTRA_TEXT);

        /**
         * This is to randomly select pictures as the background based on the different time of the day,
         * that are, morning, afternoon, evening, and give user the different greeting.
         */
        View v=(View)findViewById(R.id.bg_home);
        String[] morningPic={"morning"};
        String[] afternoonPic={"afternoon2","afternoon_meitu_4","road2_meitu_2"};
        String[] eveningPic={"evening_meitu_5","night2","road1_meitu_2_meitu_3"};
        String backg;
        if (hourInt>=4 && hourInt<12) {
            home_greeting.setText("Good morning, "+username+"!");
            int random=(int)(Math.random()*(morningPic.length));
            backg=morningPic[random];
        }
        else {
            if (hourInt >= 12 && hourInt < 18){
                home_greeting.setText("Good afternoon, " + username + "!");
                int random=(int)(Math.random()*(afternoonPic.length));
                backg=afternoonPic[random];
            }
            else {
                home_greeting.setText("Good evening, " + username + "!");
                int random=(int)(Math.random()*(eveningPic.length));
                backg=eveningPic[random];
            }
        }
        int id=getResources().getIdentifier(backg,"drawable",getPackageName());
        v.setBackground(getDrawable(id));


        /**
         *Set a Click Listener on ImageView "profiBtn", pressing this image will jump to ProfileActivity which
         *shows the user profile.This HomeActivity will not finish after jumping to Profile.
         */
         ImageView profiBtn= (ImageView) findViewById(R.id.imageButton2);
         profiBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent profileIntent=new Intent(getApplicationContext(),ProfileActivity.class);
                 profileIntent.putExtra(Intent.EXTRA_TEXT, username);
                 startActivity(profileIntent);
             }
        });


         /**
         *Set a Click Listener on Button "startBtn2", pressing this button will jump to MainActivity which
         * has the main functions of this APP.
         */
        Button startBtn2=(Button)findViewById(R.id.home_button);
        startBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent2=new Intent(getApplicationContext(), MainActivity.class);
                startIntent2.putExtra(Intent.EXTRA_TEXT, username);
                startActivity(startIntent2);
            }
        });


    }
}
