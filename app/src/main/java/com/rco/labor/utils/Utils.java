package com.rco.labor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by Fernando on 8/27/2018.
 */

public class Utils {
    public static String getDeviceId(Context context) {
        String androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static void exitApplication(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        activity.startActivity(intent);
        activity.finish();
        System.exit(0);
    }
}
