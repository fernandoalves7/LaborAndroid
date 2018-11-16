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
import com.rco.labor.businesslogic.ServerUrl;

import java.util.ArrayList;

/**
 * Created by Fernando on 9/10/2018.
 */

public class ServersListAdapter extends BaseAdapter {
    private ArrayList<ServerUrl> items;
    private Context ctx;

    public ServersListAdapter(Context ctx, ArrayList<ServerUrl> urls) {
        this.items = urls;
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
        ServerUrl url = items.get(i);

        LayoutInflater mInflator = LayoutInflater.from(ctx);
        v = mInflator.inflate(R.layout.listitem_serverurl, null, false);

        try {
            TextView field1 = (TextView) v.findViewById(R.id.tv_url);
            field1.setText(url.getUrl());
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }

        try {
            ImageView image = (ImageView) v.findViewById(R.id.icon);

            image.setVisibility(url.isSelected() ? View.VISIBLE : View.INVISIBLE);
            image.setImageResource(R.mipmap.ic_check);
        } catch (Throwable t) {
            Log.d(BusinessRules.TAG, url.toString());

            if (t != null)
                t.printStackTrace();
        }

        return v;
    }
}
