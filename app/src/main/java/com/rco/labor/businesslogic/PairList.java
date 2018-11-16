package com.rco.labor.businesslogic;

/**
 * Created by Fernando on 9/16/2018.
 */

import java.io.Serializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PairList implements Serializable {
    private Map<String, String> map = new HashMap();

    public PairList() {

    }

    public PairList(JSONArray a) throws JSONException {
        if (a == null || a.length() == 0)
            return;

        for (int i=0; i<a.length(); i++) {
            JSONObject pair = a.getJSONObject(i);
            String displayName = pair.getString("displayName");
            String value = pair.getString("value");
            add(displayName, value);
        }
    }

    // Public methods

    public List<Pair> toList() {
        List<Pair> pairs = new Vector<Pair>();
        Iterator<String> iter = map.keySet().iterator();

        while (iter.hasNext()) {
            String key = iter.next();
            String value = map.get(key);

            if (value != null)
                pairs.add(new Pair(key, value));
        }

        return pairs;
    }

    public boolean exists(String name) {
        return getValue(name) != null;
    }

    public void add(Pair p) {
        add(p.Key, p.Value);
    }

    public void add(String key, String value) {
        map.put(key, value);
    }

    public void setMap(Map<String, String> rawmap, boolean clearPreviousValues) {
        if (clearPreviousValues)
            map.clear();

        map.putAll(rawmap);
    }

    public String getValue(String name) {
        String value = map.get(name);
        return value;
    }

    public boolean getBooleanValue(String name) {
        String value = getValue(name);
        return value != null && ( value.equalsIgnoreCase("true") || value.equals("1"));
    }

    public double getDoubleValue(String name) {
        double dv = 0;
        String value = getValue(name);

        if (value != null && value.trim().length() > 0) {
            try {
                dv = Double.parseDouble(value.trim());
            } catch(Exception ex) {
            }
        }

        return dv;
    }

    public int size() {
        return map.size();
    }

    public void remove(String key) {
        map.remove(key);
    }
}
