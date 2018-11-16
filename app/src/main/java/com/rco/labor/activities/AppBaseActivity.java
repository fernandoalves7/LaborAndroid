package com.rco.labor.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rco.labor.utils.TextUtils;

/**
 * Created by Fernando on 8/27/2018.
 */

public abstract class AppBaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

   @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
    }

    protected void showLoading(String message){
        if (mProgressDialog == null || TextUtils.isNullOrEmpty(message))
            return;

        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    protected void hideLoading(){
        if (mProgressDialog == null)
            return;

        mProgressDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
