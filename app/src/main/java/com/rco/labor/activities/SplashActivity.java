package com.rco.labor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rco.labor.R;
import com.rco.labor.businesslogic.BusinessRules;
import com.rco.labor.utils.TimerUtils;

public class SplashActivity extends Activity {
    private BusinessRules rules = BusinessRules.instance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TimerUtils.startTimer(500, 1000, new Runnable() {
            public void run() {
                rules.instatiateDatabase(getApplicationContext());

                startActivity(new Intent(SplashActivity.this, com.rco.labor.activities.LoginActivity.class));
                TimerUtils.stopTimer();
                finish();
            }
        });
    }
}
