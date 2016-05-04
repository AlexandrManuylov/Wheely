package org.chaynik.wheely.model.geo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;

import org.chaynik.wheely.WheelyApp;
import org.chaynik.wheely.utils.GPSConfiguration;
import org.chaynik.wheely.utils.WheelyUtils;

public class GPSLocationReceiver extends BroadcastReceiver {
    private final static String TAG = "GPSLocationReceiver";
    public final static String CONFIG_TAG = "gps_config";
    public final static String GPS_RECEIVED_TAG = "GPS_Location_Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Intent locationBroadcast = new Intent(GPS_RECEIVED_TAG);
            if (WheelyUtils.isGeoDisabled()){
                locationBroadcast.putExtra(CONFIG_TAG, GPSConfiguration.GPS_DISABLED);
            } else {
                locationBroadcast.putExtra(CONFIG_TAG, GPSConfiguration.GPS_ENABLED);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(locationBroadcast);

        }
    }



}