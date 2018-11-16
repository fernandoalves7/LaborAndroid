package com.rco.labor.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewStub;

import com.rco.labor.R;

/**
 * Created by Fernando on 8/27/2018.
 */

public abstract class BaseCoordinatorActivity extends AppBaseActivity {
    protected abstract void onActivityCreated();
    protected abstract void onToolbarHomeButtonClicked();

    protected int getSecondaryToolbarId(){
        return 0;
    }

    protected int getToolbarId(){
        return R.layout.toolbar_container_wos;
    }

    protected int getContentLayoutId(){
        return 0;
    }

    protected int getBottomLayoutId() {
        return 0;
    }

    protected boolean isShowHomeButton() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_coordinator);

        try {
            ViewStub tabViewStub = findViewById(R.id.stub_tabbar);

            if (getSecondaryToolbarId() > 0) {
                tabViewStub.setLayoutResource(getSecondaryToolbarId());
                tabViewStub.inflate();
            }

            ViewStub toolbarViewStub = findViewById(R.id.stub_toolbar);
            toolbarViewStub.setLayoutResource(getToolbarId());
            toolbarViewStub.inflate();
            initActionbar();
            ViewStub contentViewStub = findViewById(R.id.stub_content);

            if (getContentLayoutId() > 0) {
                contentViewStub.setLayoutResource(getContentLayoutId());
                contentViewStub.inflate();
            }

            ViewStub stub_bottom_layout = findViewById(R.id.stub_fixed_bottom_layout);

            if (getBottomLayoutId() > 0) {
                stub_bottom_layout.setLayoutResource(getBottomLayoutId());
                stub_bottom_layout.inflate();
            }

            onActivityCreated();
        } catch (Throwable t) {
        }
    }

    public void initActionbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isShowHomeButton());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onToolbarHomeButtonClicked();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
