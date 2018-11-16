package com.rco.labor.activities.labor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rco.labor.R;
import com.rco.labor.activities.BaseCoordinatorActivity;
import com.rco.labor.adapters.LaborListAdapter;
import com.rco.labor.businesslogic.BusinessRules;
import com.rco.labor.businesslogic.labor.Labor;

import java.util.ArrayList;

/**
 * Created by Fernando on 8/27/2018.
 */
public class LaborMainActivity extends BaseCoordinatorActivity implements AdapterView.OnItemClickListener {
    private BusinessRules rules = BusinessRules.instance();
    private ArrayList<Labor> labors;
    private LaborListAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadListView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
        TextView field1 = v.findViewById(R.id.tv_field1);
        String laborEmployeeId = labors.get(i).getEmployeeId();

        Intent intent = new Intent(LaborMainActivity.this, LaborDetailActivity.class);
        intent.putExtra("LaborEmployeeID", laborEmployeeId);

        startActivity(intent);
    }

    @Override
    protected void onActivityCreated() {
        setTitle(R.string.labor_title);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getTitle());

        ImageView moreBtn = findViewById(R.id.more_button);
        moreBtn.setVisibility(View.INVISIBLE);

        ImageView searchClear = findViewById(R.id.search_clear_icon);
        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText searchBox = findViewById(R.id.search_box);
                searchBox.setText("");
            }
        });

        ((EditText) findViewById(R.id.search_box)).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { loadListView(); }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        loadListView();
    }

    @Override
    protected void onToolbarHomeButtonClicked() {

    }

    @Override
    protected boolean isShowHomeButton() {
        return false;
    }

    @Override
    protected int getToolbarId() {
        return R.layout.activity_toolbar;
    }

    @Override
    protected int getSecondaryToolbarId() {
        return R.layout.search_bar;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.labor_main;
    }

    private void loadListView() {
        EditText searchBox = findViewById(R.id.search_box);
        String searchText = searchBox.getText().toString();

        labors = rules.getLabors(searchText);

        adapter = new LaborListAdapter(this, labors);
        listView = findViewById(R.id.list);

        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

    public void initActionbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isShowHomeButton());
    }
}
