package org.chaynik.wheely.preferences;

import android.content.SharedPreferences;


public class Profile extends Preferences {
    private static final String FIRST_NAME = "user_name";
    private static final String USER_PASSWORD = "user_password";

    public static String getStringData(String prefsToken) {
        return getProfilePreference().getString(prefsToken, "");
    }

    public static String getFloatData(String prefsToken) {
        return getProfilePreference().getString(prefsToken, "");
    }

    public static void saveUserInfo(String userName, String password) {
        SharedPreferences.Editor editor = getProfileEditor();
        editor.putString(FIRST_NAME, userName != null ? userName : "");
        editor.putString(USER_PASSWORD, password != null ? password : "");
        save(editor);
    }

    public static String getUserName() {
        return getStringData(FIRST_NAME);
    }

    public static String getUserPassword() {
        return getStringData(USER_PASSWORD);
    }
}
