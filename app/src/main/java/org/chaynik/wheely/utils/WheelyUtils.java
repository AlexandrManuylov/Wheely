package org.chaynik.wheely.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;

import org.chaynik.wheely.BuildConfig;
import org.chaynik.wheely.WheelyApp;

public class WheelyUtils {

    public static boolean isServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidProfile(String text) {
        return text != null && !text.isEmpty() && text.substring(0, 1).equals("a");
    }

    public static void logD(String tag, String text) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, text == null ? "null" : text);
        }
    }
    private static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static String objectToJson(Object object){
        return WheelyApp.getInstance().getGson().toJson(object);
    }
}
