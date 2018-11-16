package com.rco.labor.businesslogic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rco.labor.R;
import com.rco.labor.businesslogic.labor.Labor;
import com.rco.labor.businesslogic.rms.Rms;
import com.rco.labor.businesslogic.rms.User;
import com.rco.labor.utils.DatabaseHelper;
import com.rco.labor.utils.TextUtils;
import com.rco.labor.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by Fernando on 8/24/2018.
 */
public class BusinessRules {
    private static BusinessRules instance;
    private boolean isDebugMode = false;

    public static synchronized BusinessRules instance() {
        if (instance == null) {
            instance = new BusinessRules();

            Rms.orgNumber = ORG_NUMBER;
            Rms.orgName = ORG_NAME;
        }

        return instance;
    }

    //region Database management

    private static DatabaseHelper db;
    private static Context lastCtx;

    public static void openDatabase() {
        db.open();
    }

    public void instatiateDatabase(Context ctx) {
        ArrayList<String> tables = new ArrayList<String>();

        tables.add("settings");
        tables.add("users");

        db = new DatabaseHelper(ctx, "mobileoffice.db", 1, tables, new String[] {
            "CREATE TABLE settings (" +
                "id						INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "key					TEXT NOT NULL, " +
                "value					TEXT" +
            ");",
            "CREATE TABLE users (" +
                "id						INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recordId				TEXT NOT NULL, " +
                "firstName				TEXT NOT NULL, " +
                "lastName				TEXT NOT NULL, " +
                "employeeId				TEXT, " +
                "itemType				TEXT NOT NULL" +
            ");"
        });
    }

    public static void closeDatabase() {
        db.close();
    }

    //endregion

    //region General

    public boolean existsLastLoggedInUsername() {
        String username = getLastLoggedInUsername();
        return username.length() > 0;
    }

    public String getLastLoggedInUsername() {
        String lastLoggedInUsername = getSetting("lastloggedinusername");

        if (lastLoggedInUsername == null)
            return "";

        return lastLoggedInUsername.trim();
    }

    public void setLastLoggedInUsername(String value) {
        setSetting("lastloggedinusername", value);
    }

    public String getLastLoggedInUserPassword() {
        String password = getSetting("lastloggedinuserpassword");

        if (password == null)
            return "";

        return password.trim();
    }

    public void setLastLoggedInUserPassword(String value) {
        setSetting("lastloggedinuserpassword", value);
    }

    public void clearLastLoggedInUserPassword() {
        setSetting("lastloggedinuserpassword", "");
    }

    public void switchRememberUserPassword() {
        boolean rememberPassword = rememberUserPassword();
        setSetting("rememberuserpassword", !rememberPassword ? "true" : "false");
    }

    public boolean rememberUserPassword() {
        String rememberPasswordStr = getSetting("rememberuserpassword");
        return rememberPasswordStr != null && rememberPasswordStr.equalsIgnoreCase("true");
    }

    public boolean showPassword() {
        String showPasswordStr = getSetting("showpassword");
        return showPasswordStr != null && showPasswordStr.equalsIgnoreCase("true");
    }

    public void switchShowPassword() {
        boolean showPassword = showPassword();
        setSetting("showpassword", !showPassword ? "true" : "false");
    }

    //endregion

    //region DeviceID

    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Context ctx) {
        deviceId = Utils.getDeviceId(ctx);
    }

    //endregion

    //region Server URLs

    private ArrayList<ServerUrl> serverUrls;

    public void setServerUrls() {
        serverUrls = new ArrayList();

        serverUrls.add(new ServerUrl("https://www.rcofox.com", true));
        serverUrls.add(new ServerUrl("https://www.rcolion.com", false));
        serverUrls.add(new ServerUrl("https://www.rcofalcon.com", false));
        serverUrls.add(new ServerUrl("http://saturn", false));
    }

    public ArrayList<ServerUrl> getServerUrls() {
        if (serverUrls == null)
            setServerUrls();

        return serverUrls;
    }

    public String getSelectedServerUrlStr() {
        if (serverUrls == null)
            setServerUrls();

        for (int i=0; i<serverUrls.size(); i++) {
            ServerUrl url = serverUrls.get(i);
            if (url.isSelected())
                return url.getUrl();
        }

        return null;
    }

    public void setSelectedServerUrl(String serverNameMatch) {
        if (serverUrls == null)
            return;

        for (int i=0; i<serverUrls.size(); i++) {
            ServerUrl url = serverUrls.get(i);
            url.setSelected(false);

            if (url.getUrl() != null && url.getUrl().toLowerCase().indexOf(serverNameMatch.toLowerCase()) != -1) {
                url.setSelected(true);
                Rms.setUrl(url.getUrl());
                Log.d(TAG, ">>>" + Rms.getUrl());
            }
        }
    }

    public void setSelectedServerUrl(int index) {
        if (serverUrls == null)
            return;

        for (int i=0; i<serverUrls.size(); i++) {
            ServerUrl url = serverUrls.get(i);
            url.setSelected(i == index);

            if (i == index) {
                Rms.setUrl(url.getUrl());
                Log.d(TAG, ">>>" + Rms.getUrl());
            }
        }
    }

    //endregion

    //region Debug mode

    public boolean isProductionMode() {
        return !isDebugMode();
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public String getDebugUsername() {
        return "dee";
    }

    public String getDebugPassword() {
        return "123456789";
    }

    //endregion

    //region Sync

    private List<User> users = new ArrayList<User>();

    private User user;
    private String username;
    private String password;

    public User authenticate(String username, String password) throws Exception {
        //{"firstName":"Henry","lastName":"Catalan","userId":"17062","objectId":"17062","objectType":"User"}

        this.username = username;
        this.password = password;

        String response = Rms.getUserInfo(username, password);

        if (response == null || response.trim().length() == 0)
            return null;

        JSONObject jsonObj = new JSONObject(response);
        user = new User(Rms.parseJsonCodingFields(jsonObj));

        return user;
    }

    public void setUsernamePasswordIdentifier(String username, String password) {
        Rms.setUsernamePasswordIdentifier(username, password,1);
    }

    public void syncLaborTimecard(Labor l) throws Exception {
        Rms.setTimecards(getDeviceId(), username, user, l);
    }

    public void clearLaborTimecard(Labor l) {
        l.clearClockManager();
    }

    public ArrayList<User> syncUsers(String itemType) throws Exception {
        ArrayList<User> result = new ArrayList<User>();

        String response = Rms.getRecordsUpdatedXFiltered("User", -5000,
                "Organization+Name,ItemType",",", ORG_NAME + "," + itemType, ",",
                "RecordId,First+Name,Last+Name,Employee+Id");

        if (response == null || response.trim().length() == 0)
            return null;

        JSONArray jsonArray = new JSONArray(response);

        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            User u = new User(Rms.parseJsonCodingFields(o));

            u.setItemType(itemType);

            result.add(u);
        }

        return result;
    }

    public ArrayList<Labor> syncLabor() throws Exception {
        ArrayList<Labor> result = new ArrayList();

        String response = Rms.getRecordsUpdatedXFiltered("User", -5000,
                "Organization+Name,ItemType",",", ORG_NAME + ",labor", ",",
                "RecordId,First+Name,Last+Name,Employee+Id");

        if (response == null || response.trim().length() == 0)
            return null;

        JSONArray jsonArray = new JSONArray(response);

        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            Labor u = new Labor(Rms.parseJsonCodingFields(o));
            u.setItemType("labor");
            result.add(u);
        }

        Collections.sort(result, new Comparator<Labor>() {
            public int compare(Labor l1, Labor l2) {
                if (l1.getLastFirstName() == null && l2.getLastFirstName() == null)
                    return 0;

                if (l1.getLastFirstName() == null)
                    return -1;

                if (l2.getLastFirstName() == null)
                    return 1;

                return l1.getLastFirstName().compareToIgnoreCase(l2.getLastFirstName());
            }
        });

        return result;
    }

    public void clearUsers() {
        users.clear();
        db.delete("users");
    }

    public void storeLabor(ArrayList<Labor> labors) {
        if (users == null)
            return;

        this.users.addAll(labors);

        for (Labor u: labors) {
            ContentValues cols = new ContentValues();

            cols.put("recordId", u.getRecordId());
            cols.put("firstName", u.getFirstName());
            cols.put("lastName", u.getLastName());
            cols.put("employeeId", u.getEmployeeId());
            cols.put("itemType", u.getItemType());

            db.insert("users", cols);
        }
    }

    public void storeUsers(ArrayList<User> users) {
        if (users == null)
            return;

        this.users.addAll(users);

        for (User u: users) {
            ContentValues cols = new ContentValues();

            cols.put("recordId", u.getRecordId());
            cols.put("firstName", u.getFirstName());
            cols.put("lastName", u.getLastName());
            cols.put("employeeId", u.getEmployeeId());
            cols.put("itemType", u.getItemType());

            db.insert("users", cols);
        }
    }

    public void getUser() {
        Cursor c = db.getQuery("SELECT * FROM User");
        c.moveToFirst();

        while (!c.isAfterLast()) {
            String firstName = c.getString(2);
            c.moveToNext();
        }

        c.close();
    }

    public ArrayList<String> syncUserRights() throws Exception {
        String response = Rms.getUserRights();

        if (response == null || response.trim().length() == 0)
            return null;

        ArrayList<String> result = new ArrayList<String>();
        JSONArray jsonArray = new JSONArray(response);

        for (int i=0; i<jsonArray.length(); i++)
            result.add(jsonArray.getString(i));

        return result;
    }

    public void setUserRights(ArrayList<String> userRights) {
        this.userRights = userRights;
    }

    //endregion

    //region Labor

    public static boolean isLaborSearchMatch(User l, String searchText) {
        if (TextUtils.isNullOrWhitespaces(searchText))
            return true;

        searchText = TextUtils.toLowerCase(searchText);

        Vector<String> searchFields = new Vector();
        searchFields.add(l.getFirstName());
        searchFields.add(l.getLastName());
        searchFields.add(l.getRecordId());

        for (String searchField : searchFields)
            if (TextUtils.toLowerCase(searchField).indexOf(searchText) != -1)
                return true;

        return false;
    }

    public ArrayList<Labor> getLabors(String searchText) {
        ArrayList<Labor> labors = getLabors();

        if (labors == null || labors.size() == 0)
            return null;

        if (TextUtils.isNullOrWhitespaces(searchText))
            return labors;

        ArrayList<Labor> result = new ArrayList();

        for (Labor a : labors)
            if (isLaborSearchMatch(a, searchText))
                result.add(a);

        return result;
    }

    public ArrayList<Labor> getLabors() {
        ArrayList<Labor> result = new ArrayList();

        if (users == null)
            return result;

        for (int i=0; i<users.size(); i++) {
            User u = users.get(i);

            if (u instanceof Labor)
                result.add((Labor) u);
        }

        return result;
    }

    public User getUser(String employeeId) {
        if (users == null)
            return null;

        for (User u: users)
            if (u != null && u.getEmployeeId() != null && u.getEmployeeId().equalsIgnoreCase(employeeId))
                return u;

        return null;
    }

    //endregion

    //region Logging, settings, preferences

    public String getSetting(String key) {
        if (!db.exists("SELECT * FROM settings WHERE key='" + key + "'"))
            return null;

        String result = null;
        Cursor c = db.getQuery("SELECT * FROM settings WHERE key='" + key + "'");
        c.moveToFirst();

        while (!c.isAfterLast()) {
            result = c.getString(2);
            c.moveToNext();
        }

        c.close();
        return result;
    }

    public void setSetting(String key, String value) {
        boolean existsSetting = db.exists("SELECT * FROM settings WHERE key='" + key + "'");
        ContentValues values = new ContentValues();

        if (existsSetting) {
            values.put("value", value);

            db.update("settings", values, "key='" + key + "'");
        } else {
            values.put("key", key);
            values.put("value", value);

            db.insert("settings", values);
        }
    }

    public static void logDebug(String value) {
        Log.d(TAG, ">>>" + value);
        //logEntry(value);
        //rotateLog();
    }

    //endregion}

    //region Dashboard management

    public boolean loadDynamicDashboard(Activity a, LinearLayout dashboard, int orientation) {
        boolean canProceed = false;
        Vector<Tuple<Integer, Boolean>> icons = getAllowedDashboardIconsArray();

        if (icons != null && !icons.isEmpty()) {
            int colsPerRow = 2;

            if (orientation != Configuration.ORIENTATION_PORTRAIT && orientation != Configuration.ORIENTATION_SQUARE)
                colsPerRow = 3;

            int rows = icons.size() / colsPerRow + (icons.size() % colsPerRow > 0 ? 1 : 0);
            dashboard.removeAllViews();

            for (int i = 0; i < rows; i++)
                dashboard.addView(getRowIcons(a, colsPerRow, i, icons));

            setDashboardBackground(a, orientation);
            canProceed = true;
        }

        return canProceed;
    }

    private void setDashboardBackground(Activity a, int orientation) {
        try {
            View viewDashboard = a.findViewById(R.id.background);

            String strWallpaperName = TextUtils.isNullOrWhitespaces(BusinessRules.instance().getSetting("BACKGROUND_NAME")) ?
                "" : BusinessRules.instance().getSetting("BACKGROUND_NAME");

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (strWallpaperName.compareToIgnoreCase("Aluminum") == 0)
                    viewDashboard.setBackgroundResource(R.mipmap.wallpaper_aluminum_landscape);
                else if (strWallpaperName.compareToIgnoreCase("Oldpaper") == 0)
                    viewDashboard.setBackgroundResource(R.mipmap.wallpaper_oldpaper_landscape);
                else if (strWallpaperName.compareToIgnoreCase("Wood") == 0)
                    viewDashboard.setBackgroundResource(R.mipmap.wallpaper_wood_landscape);
                else if (strWallpaperName.compareToIgnoreCase("") != 0) {
                    Bitmap screenWallPaper = Bitmap.createBitmap(BitmapFactory.decodeFile(strWallpaperName));
                    viewDashboard.setBackgroundDrawable(new BitmapDrawable(screenWallPaper));
                }
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (strWallpaperName.compareToIgnoreCase("Aluminum") == 0)
                    viewDashboard.setBackgroundResource(R.mipmap.wallpaper_aluminum_portrait);
                else if (strWallpaperName.compareToIgnoreCase("Oldpaper") == 0)
                    viewDashboard.setBackgroundResource(R.mipmap.wallpaper_oldpaper_portrait);
                else if (strWallpaperName.compareToIgnoreCase("Wood") == 0)
                    viewDashboard.setBackgroundResource(R.mipmap.wallpaper_wood_portrait);
                else if (strWallpaperName.compareToIgnoreCase("") != 0) {
                    Bitmap screenWallPaper = Bitmap.createBitmap(BitmapFactory.decodeFile(strWallpaperName));
                    viewDashboard.setBackgroundDrawable(new BitmapDrawable(screenWallPaper));
                }
            }
        } catch (Exception ex) {
            // Depending on the device we may run out of memory for large wallpapers
        }
    }

    private LinearLayout getRowIcons(Activity a, int cols, int row, Vector<Tuple<Integer, Boolean>> resIds) {
        HashMap<Integer, Integer> titles = getDashboardTitlesArray();

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params1.gravity = Gravity.CENTER_VERTICAL;
        params1.weight = 1;

        LinearLayout line = new LinearLayout(a);
        line.setLayoutParams(params1);
        line.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < cols; i++) {
            int resIdIndex = (row * cols) + i;
            int resId = -1;

            if (resIdIndex < resIds.size()) // There is no more resources to show
                resId = resIds.get(resIdIndex).getElement0();

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params2.gravity = Gravity.CENTER;
            params2.weight = 1;

            LayoutInflater mInflator = LayoutInflater.from(a);
            View item = mInflator.inflate(R.layout.dashboard_item, null, false);
            item.setLayoutParams(params2);

            if (resId != -1) {
                ImageView img = item.findViewById(R.id.img_icon);
                img.setOnClickListener((View.OnClickListener) a);
                img.setImageResource(resId);
                img.setTag(resId);

                TextView tv = item.findViewById(R.id.txt_label);
                tv.setVisibility(View.VISIBLE);

                try {
                    tv.setText(titles.get(resId));
                } catch (Exception e) {
                    //Logger.logDebug("resId = " + resId);
                }
            }

            line.addView(item);
        }

        return line;
    }

    private Vector<Tuple<Integer, Boolean>> getAllowedDashboardIconsArray() {
        Vector<Tuple<Integer, Boolean>> allIcons = getDashboardIconsArray();

        if (allIcons == null)
            return null;

        Vector<Tuple<Integer, Boolean>> result = new Vector<Tuple<Integer, Boolean>>();

        for (Tuple<Integer, Boolean> icon : allIcons)
            if (icon.getElement1() == true) // Has right to access
                result.add(icon);

        return result;
    }

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> getDashboardTitlesArray() {
        HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

        /*result.put(R.mipmap.jobs, R.string.dashboard_title_jobs);
        result.put(R.mipmap.expenses, R.string.dashboard_title_expenses);
        result.put(R.mipmap.map, R.string.dashboard_title_map);
        result.put(R.mipmap.packages, R.string.dashboard_title_packages);
        result.put(R.mipmap.calendar, R.string.dashboard_title_calendar);
        result.put(R.mipmap.customers, R.string.dashboard_title_customers);
        result.put(R.mipmap.employees, R.string.dashboard_title_employees);
        result.put(R.mipmap.vendors, R.string.dashboard_title_vendors);
        result.put(R.mipmap.trucks, R.string.dashboard_title_trucks);
        result.put(R.mipmap.sales, R.string.dashboard_title_sales);
        result.put(R.mipmap.salesorder, R.string.dashboard_title_salesorder);*/
        result.put(R.mipmap.timecards, R.string.dashboard_title_timecards);
        result.put(R.mipmap.labor, R.string.dashboard_title_labor);
        /*result.put(R.mipmap.files, R.string.dashboard_title_files);
        result.put(R.mipmap.stores, R.string.dashboard_title_stores);
        result.put(R.mipmap.invoices, R.string.dashboard_title_invoices);
        result.put(R.mipmap.leads, R.string.dashboard_title_leads);
        result.put(R.mipmap.prospects, R.string.dashboard_title_prospects);
        result.put(R.mipmap.dealers, R.string.dashboard_title_dealers);
        result.put(R.mipmap.technicians, R.string.dashboard_title_technicians);*/
        result.put(R.mipmap.settings, R.string.dashboard_title_settings);
        /*result.put(R.mipmap.sensors, R.string.dashboard_title_sensors);
        result.put(R.mipmap.checkout, R.string.dashboard_title_checkout);
        result.put(R.mipmap.checkin, R.string.dashboard_title_checkin);
        result.put(R.mipmap.library, R.string.dashboard_title_library);
        result.put(R.mipmap.boxes, R.string.dashboard_title_boxes);
        result.put(R.mipmap.move, R.string.dashboard_title_move);
        result.put(R.mipmap.shipping, R.string.dashboard_title_shipping);
        result.put(R.mipmap.receiving, R.string.dashboard_title_receiving);
        result.put(R.mipmap.picklist, R.string.dashboard_title_picklist);
        result.put(R.mipmap.stockcount, R.string.dashboard_title_stockcount);
        result.put(R.mipmap.fill, R.string.dashboard_title_fill);
        result.put(R.mipmap.forms, R.string.dashboard_title_forms);
        result.put(R.mipmap.dvir, R.string.dashboard_title_dvir);
        result.put(R.mipmap.trucklogs, R.string.dashboard_title_trucklogs);
        result.put(R.mipmap.field, R.string.dashboard_title_fields);
        result.put(R.mipmap.plantings, R.string.dashboard_title_plantings);
        result.put(R.mipmap.harvest, R.string.dashboard_title_calendar);
        result.put(R.mipmap.harvest_operation, R.string.dashboard_title_harvest);
        result.put(R.mipmap.irrigation, R.string.dashboard_title_devices);
        result.put(R.mipmap.delivery, R.string.dashboard_title_delivery);*/
        result.put(R.mipmap.syncstatusyellow, R.string.dashboard_title_syncstatusyellow);
        result.put(R.mipmap.sync_now, R.string.dashboard_title_sync_now);
        /*result.put(R.mipmap.trailers, R.string.dashboard_title_trailers);
        result.put(R.mipmap.gauges, R.string.dashboard_title_gauges);
        result.put(R.mipmap.trucks, R.string.dashboard_title_trucks);
        result.put(R.mipmap.driver_settings, R.string.dashboard_title_driver_settings);*/

        return result;
    }

    private Vector<Tuple<Integer, Boolean>> getDashboardIconsArray() {
        if (savedIcons == null) {
            Vector<Tuple<Integer, Boolean>> result = new Vector<Tuple<Integer, Boolean>>();

            result.add(new Tuple<Integer, Boolean>(R.mipmap.settings, existsUserRight("Mobile-Displaysettings")));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.sync_now, existsUserRight("Mobile-DisplaySyncNow")));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.syncstatusyellow, existsUserRight("Mobile-DisplaySyncStatus")));
            /*result.add(new Tuple<Integer, Boolean>(R.mipmap.jobs, BusinessRules.existsJobsRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.expenses, BusinessRules.existsExpensesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.map, BusinessRules.existsMapRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.packages, BusinessRules.existsPackagesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.calendar, BusinessRules.existsCalendarRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.customers, BusinessRules.existsCustomersRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.employees, BusinessRules.existsEmployeesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.vendors, BusinessRules.existsVendorsRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.sales, BusinessRules.existsSalesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.salesorder, BusinessRules.existsSalesOrderRight()));*/
            //result.add(new Tuple<Integer, Boolean>(R.mipmap.timecards, existsUserRight("Mobile-Displayvendortimecardsloginhasaccessto")));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.labor, existsUserRight("Mobile-DisplayLabor")));
            /*result.add(new Tuple<Integer, Boolean>(R.mipmap.files, BusinessRules.existsFilesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.stores, BusinessRules.existsInventoryRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.invoices, BusinessRules.existsInvoicesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.leads, BusinessRules.existsLeadsRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.prospects, BusinessRules.existsProspectsRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.dealers, BusinessRules.existsDealersRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.technicians, BusinessRules.existsTechniciansRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.sensors, BusinessRules.existsCalendarRight()));// TODO sensor's right
            result.add(new Tuple<Integer, Boolean>(R.mipmap.checkout, BusinessRules.existsCheckOutRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.checkin, BusinessRules.existsCheckInRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.library, BusinessRules.existsLibraryRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.delivery, BusinessRules.existsDeliveryRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.boxes, BusinessRules.existsBoxesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.move, BusinessRules.existsMoveRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.shipping, BusinessRules.existsShippingRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.receiving, BusinessRules.existsReceivingRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.picklist, BusinessRules.existsPickListRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.stockcount, BusinessRules.existsStockCountRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.fill, BusinessRules.existsStockCountRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.forms, BusinessRules.existsCalendarRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.dvir, BusinessRules.existsTruckDvirRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.trucklogs, BusinessRules.existsTruckLogsRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.field, BusinessRules.existsFieldRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.plantings, BusinessRules.existsPlantingRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.harvest, BusinessRules.existsHarvestRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.harvest_operation, BusinessRules.existsHarvestRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.irrigation, BusinessRules.existsDevicesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.trailers, BusinessRules.existsTrailersRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.gauges, BusinessRules.existsGaugesRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.trucks, BusinessRules.existsTrucksRight()));
            result.add(new Tuple<Integer, Boolean>(R.mipmap.driver_settings, BusinessRules.existsDriverSettingsRight()));*/
            // result.add(new Tuple<Integer, Boolean>(R.mipmap.inventory, BusinessRules.existsSyncNowRight()));

            savedIcons = result;
        }

        return savedIcons;
    }

    private static Vector<Tuple<Integer, Boolean>> savedIcons = null;

    //endregion

    //region User rights

    private List<String> userRights = new ArrayList<String>();

    public boolean existsUserRight(String value) {
        if (userRights == null)
            return false;

        for (String userRight : userRights)
            if (userRight != null && userRight.toString().equalsIgnoreCase(value))
                return true;

        return false;
    }

    //endregion

    //region Constants

    public static final String TAG = "com.rco.mobileoffice";

    public static final int MAX_OFFLINE_ASSETS = 100;
    public static final int SEARCH_DELAY_MILLIS = 1250;

    public static final int OK = 1000;
    public static final int UNABLE_TO_INITIALIZE = -1000;
    public static final int UNABLE_TO_SYNC = -2000;

    public static final String ORG_NAME = "Jayleaf";
    public static final String ORG_NUMBER = "20";

    //endregion
}
