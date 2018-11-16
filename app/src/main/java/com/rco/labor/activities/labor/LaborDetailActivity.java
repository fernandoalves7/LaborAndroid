package com.rco.labor.activities.labor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rco.labor.R;
import com.rco.labor.activities.BaseCoordinatorActivity;
import com.rco.labor.businesslogic.BusinessRules;
import com.rco.labor.businesslogic.labor.Labor;
import com.rco.labor.businesslogic.labor.LaborClockManager;
import com.rco.labor.utils.StringUtils;
import com.rco.labor.utils.UiUtils;

/**
 * Created by Fernando on 9/2/2018.
 */

public class LaborDetailActivity extends BaseCoordinatorActivity implements AdapterView.OnItemClickListener {
    private BusinessRules rules = BusinessRules.instance();
    private LaborClockManager clockManager;
    private Labor labor;

    private boolean isProcessing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityCreated() {
        Intent intent = getIntent();

        if (!intent.hasExtra("LaborEmployeeID"))
            finish();

        String laborEmployeeId = intent.getStringExtra("LaborEmployeeID");

        if (StringUtils.isNullOrWhitespaces(laborEmployeeId))
            finish();

        labor = (Labor) rules.getUser(laborEmployeeId);
        clockManager = labor.getClockManager();

        setTitle("Timecard Hours");
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getTitle());

        ImageView moreBtn = findViewById(R.id.more_button);
        moreBtn.setVisibility(View.INVISIBLE);

        populateActivityFields();

        RelativeLayout bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setVisibility(rules.existsUserRight("Mobile-TimecardManager") ? View.VISIBLE : View.GONE);

        String currentStatus = clockManager.getCurrentStatus();

        if (currentStatus != null && currentStatus.equalsIgnoreCase("Clock-In")) {
            UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_in_button, View.INVISIBLE);
            UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.break_button, View.VISIBLE);
            UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_out_button, View.VISIBLE);
        } else if (currentStatus != null && currentStatus.equalsIgnoreCase("StartBreak")) {
            UiUtils.setTextView(LaborDetailActivity.this, R.id.break_button, "End Break");
            UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.break_button, View.VISIBLE);
            UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_in_button, View.INVISIBLE);
            UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_out_button, View.INVISIBLE);
        }

        UiUtils.setOnClickListener(this, R.id.clock_in_button, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_in_button, View.INVISIBLE);
                UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.break_button, View.VISIBLE);
                UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_out_button, View.VISIBLE);
                clockManager.clockIn();
                populateActivityFields();
            }
        });

        UiUtils.setOnClickListener(this, R.id.clock_out_button, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_in_button, View.VISIBLE);
                UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.break_button, View.INVISIBLE);
                UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_out_button, View.INVISIBLE);
                clockManager.clockOut();
                populateActivityFields();
            }
        });

        UiUtils.setOnClickListener(this, R.id.break_button, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView breakBtn = findViewById(R.id.break_button);

                if (breakBtn.getText().toString().equalsIgnoreCase("Break")) {
                    UiUtils.setTextView(LaborDetailActivity.this, R.id.break_button, "End Break");
                    UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_in_button, View.INVISIBLE);
                    UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_out_button, View.INVISIBLE);
                    clockManager.startBreak();
                } else {
                    UiUtils.setTextView(LaborDetailActivity.this, R.id.break_button, "Break");
                    UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_in_button, View.INVISIBLE);
                    UiUtils.setViewVisibility(LaborDetailActivity.this, R.id.clock_out_button, View.VISIBLE);
                    clockManager.endBreak();
                }

                populateActivityFields();
            }
        });

        UiUtils.setOnClickListener(this, R.id.sync_timecard_button, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentStatus = clockManager.getCurrentStatus();

                if (currentStatus != null) {
                    boolean isTimecardClocking = currentStatus.equalsIgnoreCase("Clock-In") ||
                        currentStatus.equalsIgnoreCase("StartBreak") ||
                        currentStatus.equalsIgnoreCase("EndBreak");

                    if (isTimecardClocking) {
                        UiUtils.showToast(LaborDetailActivity.this, "Cannot submit a labor timecard while clocked-in or on break.");
                        return;
                    } else if (!clockManager.existsPendingHours()) {
                        UiUtils.showToast(LaborDetailActivity.this, "Cannot submit a empty timecard.");
                        return;
                    }
                }

                LaborDetailActivity.SyncTimecardTask syncTimecardTask = new LaborDetailActivity.SyncTimecardTask();
                syncTimecardTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    protected void onToolbarHomeButtonClicked() {
        finish();
    }

    @Override
    protected int getToolbarId() {
        return R.layout.activity_toolbar;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.labor_detail;
    }

    // Data submission

    public class SyncTimecardTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            setProcessingState(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                rules.syncLaborTimecard(labor);
                return BusinessRules.OK;
            } catch (Throwable t) {
                if (t != null)
                    t.printStackTrace();

                return BusinessRules.UNABLE_TO_SYNC;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setProcessingMessage("Submitting timecard...");
        }

        @Override
        protected void onPostExecute(Integer result) {
            try {
                setProcessingState(false, false);

                switch (result) {
                    case BusinessRules.UNABLE_TO_SYNC:
                        UiUtils.showExclamationDialog(LaborDetailActivity.this, getString(R.string.app_name), getString(R.string.error_unable_to_submit_timecard),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            dialog.cancel();
                                            //LaborDetailActivity.this.finish();
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                });
                        break;

                    case BusinessRules.OK:
                    default:
                        UiUtils.showOkDialog(LaborDetailActivity.this, getString(R.string.app_name),
                            R.mipmap.ic_check, getString(R.string.timecard_submitted), false,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            rules.clearLaborTimecard(labor);
                                            LaborDetailActivity.this.finish();
                                            //dialog.cancel();
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                });
                        break;
                }
            } catch (Throwable t) {
                if (t != null)
                    t.printStackTrace();
            }
        }
    }

    // Helpers

    public void initActionbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isShowHomeButton());
    }

    private void populateActivityFields() {
        UiUtils.setTextView(LaborDetailActivity.this, R.id.tv_firstLastName, labor.getFirstLastName() + " (" + labor.getEmployeeId() + ")");
        UiUtils.setTextView(LaborDetailActivity.this, R.id.todayHours, clockManager.getTodayHoursStr());
        UiUtils.setTextView(LaborDetailActivity.this, R.id.timecardTotalHours, clockManager.getTimecardTotalHoursStr());
        UiUtils.setTextView(LaborDetailActivity.this, R.id.todayOvertime, clockManager.getTodayOvertimeStr());
        UiUtils.setTextView(LaborDetailActivity.this, R.id.todayOvertimeDouble, clockManager.getTodayOvertimeDoubleStr());
        UiUtils.setTextView(LaborDetailActivity.this, R.id.todayBreakTime, clockManager.getTodayBreakTotalHoursStr());
    }

    private void setProcessingMessage(String msg) {
        UiUtils.setTextView(LaborDetailActivity.this, R.id.loading_feedback_text, msg);
    }

    private void setProcessingState(boolean isProcessing) {
        setProcessingState(isProcessing, true);
    }

    private void setProcessingState(boolean isProcessing, boolean clearCredentials) {
        this.isProcessing = isProcessing;

        TextView clockInBtn = findViewById(R.id.clock_in_button);
        TextView clockOutBtn = findViewById(R.id.clock_out_button);
        TextView breakBtn = findViewById(R.id.break_button);
        TextView syncTimecardBtn = findViewById(R.id.sync_timecard_button);

        if (isProcessing) {
            clockInBtn.setEnabled(false);
            clockOutBtn.setEnabled(false);
            breakBtn.setEnabled(false);
            syncTimecardBtn.setEnabled(false);

            findViewById(R.id.activity_panel).setEnabled(false);
            findViewById(R.id.loading_panel).setVisibility(View.VISIBLE);
        } else {
            clockInBtn.setEnabled(true);
            clockOutBtn.setEnabled(true);
            breakBtn.setEnabled(true);
            syncTimecardBtn.setEnabled(true);

            findViewById(R.id.activity_panel).setEnabled(true);
            findViewById(R.id.loading_panel).setVisibility(View.GONE);
        }
    }
}
