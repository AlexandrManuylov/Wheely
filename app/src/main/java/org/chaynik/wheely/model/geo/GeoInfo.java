package org.chaynik.wheely.model.geo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import org.chaynik.wheely.WheelyApp;
import org.chaynik.wheely.model.geo.dto.GeoData;
import org.chaynik.wheely.service.LocationService;
import org.chaynik.wheely.utils.SimpleModel;
import org.chaynik.wheely.utils.WheelyUtils;

import java.util.List;

public class GeoInfo extends SimpleModel<List<GeoData>> {
    public final static String TAG = "GeoInfo";
    public final static String GEO_INFO_RECEIVED = "geo.info.received";
    public final static String GEO_INFO_RECEIVED_TAG = "info_received";

    public void registerGeoReceiver() {
        LocalBroadcastManager.getInstance(WheelyApp.getInstance()).registerReceiver(mGeoInfoReceiver, new IntentFilter(GEO_INFO_RECEIVED));
    }

    public void unRegisterGeoReceiver() {
        LocalBroadcastManager.getInstance(WheelyApp.getInstance()).unregisterReceiver(mGeoInfoReceiver);
    }

    private BroadcastReceiver mGeoInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String location = intent.getStringExtra(GEO_INFO_RECEIVED_TAG);
                if (location != null) {
//                    GeoData geoData = new GeoData(location.getLatitude(), location.getLongitude());
//                    String message = WheelyUtils.objectToJson(geoData);

                    notifyListeners();

                }
            }
        }
    };
}
