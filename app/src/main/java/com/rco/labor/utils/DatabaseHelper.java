package com.rco.labor.utils;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String TAG = "com.rco.mobileoffice.database";

    private ArrayList<String> tablenames;
    private String[] creationscript;
    private String name;
    private SQLiteDatabase cnn;
    private Context ctx;

    // Database life-cycle

    public DatabaseHelper(Context context, String name, int dbversion, ArrayList<String> tablenames, String[] creationscript) {
        super(context, name, null, dbversion);

        this.ctx = context;
        this.name = name;
        this.creationscript = creationscript;
        this.tablenames = tablenames;

        this.cnn = this.getWritableDatabase();
    }

    public void createTables() {
        Log.d(TAG, "Creating tables...");

        if (creationscript != null)
            for (int i=0; i<creationscript.length; i++) {
                Log.d(TAG, "Executing query " + creationscript[i]);
                cnn.execSQL(creationscript[i]);
            }

        Log.d(TAG, "Done");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database " + name + "...");

        if (creationscript != null)
            for (int i=0; i<creationscript.length; i++)
                db.execSQL(creationscript[i]);

        Log.d(TAG, "Done");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database " + name + " (" + oldVersion + " -> " + newVersion + ")...");

        dropAllTables(db);
        onCreate(db);

        Log.d(TAG, "Done with upgrade");
    }

    // State management

    public boolean exists() {
        Log.d(TAG, "Checking if database " + name + " exists...");

        SQLiteDatabase cnn2 = null;
        boolean result = false;

        try {
            String path = ctx.getDatabasePath(name).getAbsolutePath();
            Log.d(TAG, path);

            cnn2 = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            result = true;
        } catch (SQLiteException ex) {

        }

        if (cnn2 != null)
            cnn2.close();

        Log.d(TAG, "Exists:" + (result ? "T" : "F"));
        return result;
    }

    public void open() {
        Log.d(TAG, "Opening database " + name + "...");

        if (cnn != null)
            return;

        String path = ctx.getDatabasePath(name).getAbsolutePath();
        cnn = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);

        Log.d(TAG, "Done");
    }

    public void close() {
        if (cnn != null) {
            cnn.close();
            cnn = null;
        }
    }

    public String getName() {
        return name;
    }

    public SQLiteDatabase getConnection() {
        return cnn;
    }

    public Context getContext() {
        return ctx;
    }

    // DML

    public boolean exists(String sqlString) {
        Cursor c = getQuery(sqlString);

        if (c == null || c.getCount() == 0)
            return false;

        //Log.d(TAG, "Exists: " + sqlString + " := " + (c.getCount() > 0 ? "true" : "false"));
        return c.getCount() > 0;
    }

    public int count(String sqlString) {
        Log.d(TAG, "Count: " + sqlString);

        Cursor c = getQuery(sqlString);

        if (c == null || c.getCount() == 0)
            return 0;

        Log.d(TAG, "Total: " + c.getCount());
        return c.getCount();
    }

    public Cursor getQuery(String sqlString) {
        if (cnn == null)
            open();

        //Log.d(TAG, "Query: " + sqlString);
        return cnn.rawQuery(sqlString, null);
    }

    public Cursor getRow(String sqlString) {
        if (cnn == null)
            open();

        Log.d(TAG, "Query: " + sqlString);
        Cursor c = cnn.rawQuery(sqlString, null);
        c.moveToFirst();

        return c;
    }

    public void insert(String tablename, ContentValues values) {
        if (cnn == null)
            open();

        Log.d(TAG, "Insert: " + tablename);
        cnn.insert(tablename, null, values);
    }

    public int update(String tablename, ContentValues values, String whereClause) {
        if (cnn == null)
            open();

        Log.d(TAG, "Update: " + tablename + " " + print(values) + " WHERE " + whereClause);
        return cnn.update(tablename, values, whereClause, null);
    }

    public int delete(String tablename) {
        if (cnn == null)
            open();

        Log.d(TAG, "Delete: " + tablename);
        return cnn.delete(tablename, null, null);
    }


    public int delete(String tablename, String whereClause) {
        if (cnn == null)
            open();

        Log.d(TAG, "Delete: " + tablename + " WHERE " + whereClause);
        return cnn.delete(tablename, whereClause, null);
    }

    // DDL

    public void dropAllTables(SQLiteDatabase db) {
        Log.d(TAG, "Dropping all tables...");

        for (int i=0; i<tablenames.size(); i++) {
            Log.d(TAG, "Dropping " + tablenames.get(i) + "...");
            db.execSQL("DROP TABLE IF EXISTS " + tablenames.get(i));
        }

        Log.d(TAG, "Done");
    }

    public void dropAllTables() {
        Log.d(TAG, "Dropping all tables...");

        for (int i=0; i<tablenames.size(); i++) {
            Log.d(TAG, "Dropping " + tablenames.get(i) + "...");
            cnn.execSQL("DROP TABLE IF EXISTS " + tablenames.get(i));
        }

        Log.d(TAG, "Done");
    }

    // Helpers

    private String print(ContentValues contentValues) {
        try {
            if (contentValues == null)
                return null;

            StringBuilder result = new StringBuilder();

            for (String key : contentValues.keySet())
                result.append(key).append(": ").append(contentValues.get(key));

            return result.toString();
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }

        return null;
    }
}
