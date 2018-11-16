package com.rco.labor.utils;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Fernando on 8/25/2018.
 */

public class TimerUtils {
    private static final Handler handler = new Handler();
    private static Timer timer;
    private static TimerTask timerTask;

    public static Timer startTimer(int delay, int period, final Runnable r) {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(r);
            }
        };

        timer.schedule(timerTask, delay, period);
        return timer;
    }

    public static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
