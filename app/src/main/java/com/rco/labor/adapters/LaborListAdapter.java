package com.rco.labor.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rco.labor.R;
import com.rco.labor.businesslogic.BusinessRules;
import com.rco.labor.businesslogic.labor.Labor;
import com.rco.labor.businesslogic.labor.LaborClockManager;

import java.util.ArrayList;

/**
 * Created by Fernando on 9/2/2018.
 */
public class LaborListAdapter extends BaseAdapter {
    private Context ctx;
    ArrayList<Labor> items;

    public LaborListAdapter(Context ctx, ArrayList<Labor> labors) {
        this.items = labors;
        this.ctx = ctx;
    }

    public int getCount() {
        return items == null ? 0 : items.size();
    }

    public Object getItem(int i) {
        return items.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View v, ViewGroup viewGroup) {
        Labor a = items.get(i);

        LayoutInflater mInflator = LayoutInflater.from(ctx);
        v = mInflator.inflate(R.layout.listitem_labor, null, false);

        try {
            TextView field1 = v.findViewById(R.id.tv_field1);
            field1.setText(a.getLastFirstName());
            field1.setTag(1, a.getEmployeeId());
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }

        try {
            TextView field2 = v.findViewById(R.id.tv_field2);
            field2.setText(a.getEmployeeId());
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }

        try {
            LaborClockManager clockManager = a.getClockManager();

            ImageView image = v.findViewById(R.id.icon);

            if (clockManager == null) {
                image.setImageResource(R.mipmap.labor_off);
                return v;
            }

            String currentStatus = clockManager.getCurrentStatus();

            if (currentStatus == null) {
                image.setImageResource(R.mipmap.labor_off);
                return v;
            }

            if (currentStatus.equalsIgnoreCase("Clock-In")) {
                image.setImageResource(R.mipmap.labor_working);
                return v;
            }

            if (currentStatus.equalsIgnoreCase("StartBreak")) {
                image.setImageResource(R.mipmap.labor_vacation);
                return v;
            }

            image.setImageResource(R.mipmap.labor_off);
        } catch (Throwable t) {
            Log.d(BusinessRules.TAG, a.toString());

            if (t != null)
                t.printStackTrace();
        }

        return v;
    }
}
