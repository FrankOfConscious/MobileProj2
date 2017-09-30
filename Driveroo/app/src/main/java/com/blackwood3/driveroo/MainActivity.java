package com.blackwood3.driveroo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView timeTv;
    TextView disTv;
    Button startBtn2;
    Button endBtn;

    Chronometer mChronometer;
    Thread mChronoThread;
    Context mainContext;

    Boolean chronoIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = this;

        timeTv = (TextView) findViewById(R.id.timeTv);
        disTv = (TextView) findViewById(R.id.disTv);

        startBtn2 = (Button) findViewById(R.id.startBtn2);
        endBtn = (Button) findViewById(R.id.endBtn);
        chronoIsRunning = false;

        startBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chronoIsRunning) {

                    if (mChronometer == null) {
                        mChronometer = new Chronometer(mainContext);
                        mChronoThread = new Thread(mChronometer);
                        mChronoThread.start();
                        mChronometer.start();

                        chronoIsRunning = true;
                        startBtn2.setText("Pause");
                    }

                } else {

                    if (mChronometer != null) {
                        mChronometer.stop();
                        mChronoThread.interrupt();
                        mChronoThread = null;
                        mChronometer = null;

                        chronoIsRunning = false;
                        startBtn2.setText("Start");
                    }
                }


            }
        });
    }

    public void updateTimeText(final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "Time: ";
                text += time;
                timeTv.setText(text);
            }
        });
    }
}
