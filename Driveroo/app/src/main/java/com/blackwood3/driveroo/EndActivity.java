package com.blackwood3.driveroo;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class EndActivity extends AppCompatActivity {

    Button vibrationBtn;
    Button soundBtn;
    MediaPlayer mediaPlayer;
    View mView;
    TextView timeTv;
    TextView distanceTv;
    TextView warningTv;

    String rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        vibrationBtn = (Button) findViewById(R.id.vibrationBtn);
        soundBtn = (Button) findViewById(R.id.soundBtn);
        timeTv = (TextView) findViewById(R.id.endTimeTv);
        distanceTv = (TextView) findViewById(R.id.endDistanceTv);
        warningTv = (TextView) findViewById(R.id.endWarnTv);
        mediaPlayer = MediaPlayer.create(this, R.raw.warn1);

        Bundle extras = getIntent().getExtras();
        String timeText = extras.getString("time");
        String distanceText = extras.getString("distance");

        timeTv.setText(timeText);
        distanceTv.setText(distanceText);


        // following is used to parse the data from Azure
//        switch (rank) {
//            case "S":
//                mView.setBackgroundColor(getResources().getColor(R.color.googleGreen));
//                break;
//            case "A":
//                mView.setBackgroundColor(getResources().getColor(R.color.googleBlue));
//                break;
//            case "B":
//                mView.setBackgroundColor(getResources().getColor(R.color.googleYellow));
//                break;
//            case "C":
//                mView.setBackgroundColor(getResources().getColor(R.color.googleRed));
//            default:
//                mView.setBackgroundColor(getResources().getColor(R.color.liamCyan));
//                Log.d("Set background color", "something wrong");
//                break;
//
//        }
        vibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EndActivity.this, "Vibration test!", Toast.LENGTH_SHORT).show();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }
        });

        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EndActivity.this, "Sound test!", Toast.LENGTH_SHORT).show();
//                try {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone rington = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                    rington.play();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                mediaPlayer.start();
            }
        });
    }

    private String[] ParseJson(JSONObject jsonObject) {
        String[] res = new String[2];
        return res;
    }
}
