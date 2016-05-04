package org.chaynik.wheely.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.chaynik.wheely.BuildConfig;
import org.chaynik.wheely.WheelyApp;
import org.chaynik.wheely.model.geo.dto.GeoData;

import java.lang.reflect.Type;
import java.util.List;

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
    public static int dpToPx(float dp) {
        // add 0.5 to round up
        return (int) ((dp * WheelyApp.getInstance().getResources().getDisplayMetrics().density) + 0.5);
    }


    public static int pxToDp(int px) {
        // add 0.5 to round up
        return (int) ((px / WheelyApp.getInstance().getResources().getDisplayMetrics().density) + 0.5);
    }
    private static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String objectToJson(Object object) {
        return WheelyApp.getInstance().getGson().toJson(object);
    }

//    public static <T> T jsonToObject(String jsonString, Type type, Class<T> tClass) {
//        return tClass.cast(WheelyApp.getInstance().getGson().fromJson(jsonString, type));
//    }

    public static boolean isLocationPermissionGranted(Context ctx) {
        return ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isGeoDisabled() {
        LocationManager locationManager = (LocationManager) WheelyApp.getInstance().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGeoDisabled = !isGPSEnabled && !isNetworkEnabled;
        return isGeoDisabled;
    }
}
