package com.paraflow.courthall;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;



public class AutoRequest {

    private static Context context;
    private static final String TAG_TISHO = "tisho";

    public static void startAlarm(Context c) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                Log.e(TAG_TISHO, "________");
            }

        },0,5000);//Update text every second


    }







}
