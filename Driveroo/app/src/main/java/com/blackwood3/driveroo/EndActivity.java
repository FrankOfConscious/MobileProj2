package com.blackwood3.driveroo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EndActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    View mView;
    TextView timeTv;
    TextView distanceTv;
    TextView warningTv;
    TextView rateTv;
    TextView rateCommentTv;
    TextView userTv;
    TextView greetTv;

    String rate1;
    String rate2;
    String rate3;
    String rate4;

    String username;
    String rate;
    String warningTime;

    String rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        mView = new View(this);
        timeTv = (TextView) findViewById(R.id.endTimeTv);
        distanceTv = (TextView) findViewById(R.id.endDistanceTv);
        warningTv = (TextView) findViewById(R.id.endWarnTv);
        rateTv = (TextView) findViewById(R.id.rateTv);
        rateCommentTv = (TextView) findViewById(R.id.ratecommentTv);
        userTv = (TextView) findViewById(R.id.userTv);
        greetTv = (TextView) findViewById(R.id.greetTv);

        rate1 = "Excellent! You are the boss!";
        rate2 = "Good! You are a reliable driver!" ;
        rate3 = "Not bad, focus more on the road!";
        rate4 = "Umm.. do not get distracted when driving next time.";
        Bundle extras = getIntent().getExtras();
        String timeText = extras.getString("time");
        String distanceText = extras.getString("distance");
        final String username = extras.getString("username");

        timeTv.setText(timeText);
        distanceTv.setText(distanceText);

        new Thread(runnable_getResult).start();

        Button backToMain=(Button)findViewById(R.id.backToMain);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jumpToHome=new Intent(getApplicationContext(), HomeActivity.class);
                jumpToHome.putExtra(Intent.EXTRA_TEXT, username);
                startActivity(jumpToHome);
                finish();
            }
        });


        // following is used to parse the data from Azure
        while(rate == null) {

        }

        switch (rate) {
            case "S":
                mView.setBackgroundColor(getResources().getColor(R.color.googleGreen));
                greetTv.setText("Hi, " + username);
                rateTv.setText(rate);
                rateCommentTv.setText(rate1);
                if (Integer.parseInt(warningTime) > 1) {
                    warningTv.setText(warningTime + " times");
                } else {
                    warningTv.setText(warningTime + " time");
                }
                userTv.setText("You are an expert driver! Keep it.");
                timeTv.setText(timeText);
                distanceTv.setText(distanceText);
                break;
            case "A":
                mView.setBackgroundColor(getResources().getColor(R.color.googleBlue));
                greetTv.setText("Hi, " + username);
                rateTv.setText(rate);
                rateCommentTv.setText(rate2);
                warningTv.setText(warningTime);
                timeTv.setText(timeText);
                distanceTv.setText(distanceText);
                userTv.setText("Good job mate, keep going!");
                break;
            case "B":
                mView.setBackgroundColor(getResources().getColor(R.color.googleYellow));
                greetTv.setText("Hi, " + username);
                rateTv.setText(rate);
                rateCommentTv.setText(rate3);
                warningTv.setText(warningTime);
                timeTv.setText(timeText);
                distanceTv.setText(distanceText);
                userTv.setText("Not bad mate, you could do better!");
                break;
            case "C":
                mView.setBackgroundColor(getResources().getColor(R.color.googleRed));
                greetTv.setText("Hi, " + username);
                rateTv.setText(rate);
                rateCommentTv.setText(rate4);
                warningTv.setText(warningTime);
                timeTv.setText(timeText);
                distanceTv.setText(distanceText);
                userTv.setText("Take care mate, life is the first!");
                break;
            default:
                mView.setBackgroundColor(getResources().getColor(R.color.liamCyan));
                Log.d("Set background color", "something wrong");
                break;

        }

    }

    Runnable runnable_getResult = new Thread(new Runnable() {
        @Override
        public void run() {
            Bundle extras = getIntent().getExtras();
            username = extras.getString("username");
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            JSONObject get_result = null;
            try{
                get_result = HttpUtils.submitGETData(params, "utf-8", "get_result");
                warningTime = get_result.getString("waring_times");
                rate = get_result.getString("index");

                Log.w("Warning times:",warningTime);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    });
}
