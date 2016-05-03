package org.chaynik.wheely;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.gson.Gson;

import org.chaynik.wheely.model.Model;

public class WheelyApp extends Application {
    private static WheelyApp mInstance;
    private Gson mGson;
    private Model mModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mGson = new Gson();
        mModel = new Model();

    }

    public Gson getGson() {
        return mGson;
    }

    public static WheelyApp getInstance() {
        return mInstance;
    }
    public Model getModel(){
        return mModel;
    }
}
