package org.chaynik.wheely;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class WheelyApp extends Application {
    private static WheelyApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    public static WheelyApp getInstance() {
        return mInstance;
    }
}
