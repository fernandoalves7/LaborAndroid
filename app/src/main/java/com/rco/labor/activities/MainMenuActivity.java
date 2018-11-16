package com.rco.labor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rco.labor.R;
import com.rco.labor.activities.labor.LaborMainActivity;
import com.rco.labor.businesslogic.BusinessRules;
import com.rco.labor.utils.Utils;

/**
 * Created by Fernando on 8/25/2018.
 */

public class MainMenuActivity extends Activity implements View.OnClickListener {
    private BusinessRules rules = BusinessRules.instance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
    }

    @Override
    public void onResume() {
        super.onResume();
        int currentOrientation = getResources().getConfiguration().orientation;

        rules.setDeviceId(this);
        rules.loadDynamicDashboard(this, (LinearLayout) findViewById(R.id.activity_dashboard), currentOrientation);
    }

    @Override
    public void onBackPressed() {
        Utils.exitApplication(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onClick(View view) {
        int resId = view instanceof ImageView ? (Integer) view.getTag() : view.getId();

        switch (resId) {
            case R.mipmap.labor:
                startActivity(new Intent(this, LaborMainActivity.class));
                break;

            /*case R.drawable.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.drawable.syncstatusyellow:
            case R.drawable.sync_now: {
                Intent intent = new Intent(this, SyncStatus.class);
                intent.putExtra(SyncStatus.SYNC_NOW, resId == R.drawable.sync_now);
                startActivity(intent);
                break;
            }*/
        }
    }
}
