package org.chaynik.wheely;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.gson.Gson;

public class WheelyApp extends Application {
    private static WheelyApp mInstance;
    private Gson mGson;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mGson = new Gson();

    }

    public Gson getGson() {
        return mGson;
    }

    public static WheelyApp getInstance() {
        return mInstance;
    }
}
