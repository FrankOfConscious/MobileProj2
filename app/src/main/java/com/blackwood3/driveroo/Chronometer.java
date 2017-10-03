package com.blackwood3.driveroo;

import android.content.Context;

/**
 * Created by liam on 27/9/17.
 */

public class Chronometer implements Runnable {

    public static final long MILLIS_TO_MINS = 60000;
    public static final long MILLIS_TI_HOURS = 3600000;

    //asdasasas

    private Context cContext;
    private long cStartTime;

    private Boolean isRunning;

    public Chronometer(Context context) {

        cContext = context;
    }

    public void start() {

        cStartTime = System.currentTimeMillis();
        isRunning = true;
    }

    public void stop() {

        isRunning = false;
    }
    @Override
    public void run() {

        while(isRunning) {

            long since = System.currentTimeMillis() - cStartTime;

            int seconds = (int) ((since / 1000) % 60);
            int minutes = (int) ((since / MILLIS_TO_MINS) % 60);
            int hours = (int) ((since / MILLIS_TI_HOURS) % 24);

            ((MainActivity)cContext).updateTimeText(String.format(
                    "%02d:%02d:%02d", hours, minutes, seconds
            ));

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
