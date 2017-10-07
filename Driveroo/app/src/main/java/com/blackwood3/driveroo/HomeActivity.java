package com.blackwood3.driveroo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.blackwood3.driveroo.R.drawable.evening_meitu_5;

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
//                home_greeting.setTextColor((this.getResources().getColor(R.color.colorPrimaryDark)));
//                home_greeting2.setTextColor((this.getResources().getColor(R.color.colorPrimaryDark)));
            }
            else {
                home_greeting.setText("Good evening, " + username + "!");
                int random=(int)(Math.random()*(eveningPic.length));
                backg=eveningPic[random];
            }
        }

        int id=getResources().getIdentifier(backg,"drawable",getPackageName());
        v.setBackground(getDrawable(id));


        ImageView profiBtn= (ImageView) findViewById(R.id.imageButton2);
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
                startIntent2.putExtra(Intent.EXTRA_TEXT, username);
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
