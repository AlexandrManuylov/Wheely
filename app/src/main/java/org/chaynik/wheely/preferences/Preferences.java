package org.chaynik.wheely.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.chaynik.wheely.WheelyApp;


public class Preferences {
    public static WheelyApp mApp = WheelyApp.getInstance();
    private static final String PROFILE_PREFERENCES_FILE = "profile_preferences";
    private static final Object mLock = new Object();


    public static void clearAllPreferences() {
        save(getProfileEditor().clear());
    }

    public static SharedPreferences getProfilePreference() {
        return mApp.getSharedPreferences(PROFILE_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getProfileEditor() {
        return getProfilePreference().edit();
    }


    public static void save(SharedPreferences.Editor editor) {
        synchronized (mLock) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }
}
