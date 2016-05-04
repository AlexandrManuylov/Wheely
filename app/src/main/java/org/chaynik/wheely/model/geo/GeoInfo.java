package org.chaynik.wheely.model.geo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.reflect.TypeToken;

import org.chaynik.wheely.WheelyApp;
import org.chaynik.wheely.model.geo.dto.GeoData;
import org.chaynik.wheely.service.WebSocketService;
import org.chaynik.wheely.utils.GPSConfiguration;
import org.chaynik.wheely.utils.ModelError;
import org.chaynik.wheely.utils.SimpleModel;
import org.chaynik.wheely.utils.WheelyUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GeoInfo extends SimpleModel<ArrayList<GeoData>> {
    public final static String TAG = "GeoInfo";

    public void registerGeoReceiver() {
        LocalBroadcastManager.getInstance(WheelyApp.getInstance()).registerReceiver(mGeoInfoReceiver, new IntentFilter(WebSocketService.GEO_INFO_RECEIVED));
        LocalBroadcastManager.getInstance(WheelyApp.getInstance()).registerReceiver(mLocationConfiguration, new IntentFilter(GPSLocationReceiver.GPS_RECEIVED_TAG));
    }

    public void unRegisterGeoReceiver() {
        LocalBroadcastManager.getInstance(WheelyApp.getInstance()).unregisterReceiver(mGeoInfoReceiver);
        LocalBroadcastManager.getInstance(WheelyApp.getInstance()).unregisterReceiver(mLocationConfiguration);
    }

    private BroadcastReceiver mGeoInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String location = intent.getStringExtra(WebSocketService.GEO_INFO_RECEIVED_TAG);
                if (location != null) {
                    Type listType = new TypeToken<ArrayList<GeoData>>() {
                    }.getType();
                    if (!WheelyUtils.isGeoDisabled()) {
                        setData((ArrayList<GeoData>) WheelyApp.getInstance().getGson().fromJson(location, listType));
                    }

                } else {
                    ModelError error = (ModelError) intent.getSerializableExtra(WebSocketService.ERROR_TAG);
                    if (error != null) {
                        setError(error);
                    }
                }
            }
        }
    };

    private BroadcastReceiver mLocationConfiguration = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                GPSConfiguration config = (GPSConfiguration) intent.getSerializableExtra(GPSLocationReceiver.CONFIG_TAG);
                if (config == GPSConfiguration.GPS_DISABLED){
                    setError(ModelError.GPS_DISABLED);
                } else {
                    mModelError = null;
                    notifyListeners();
                }
            }
        }
    };

}
