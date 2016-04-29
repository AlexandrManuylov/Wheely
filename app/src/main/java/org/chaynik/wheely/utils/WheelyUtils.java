package org.chaynik.wheely.utils;

import android.app.ActivityManager;
import android.content.Context;

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
}
