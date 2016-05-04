package org.chaynik.wheely.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.chaynik.wheely.utils.WheelyUtils;

@SuppressWarnings("MissingPermission")
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "LocationService";
    public static final String LOCATION_RECEIVED = "location.received";
    public static final String LOCATION_RECEIVED_TAG = "LOCATION";
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private final Object locking = new Object();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            WheelyUtils.logD(TAG, "Start mLocationClient");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)

                    .build();
            mGoogleApiClient.connect();
        } else {
            WheelyUtils.logD(TAG, "Start getOldLocation()");
            getOldLocation();
        }
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000); // Update location every 5 second
        if (WheelyUtils.isLocationPermissionGranted(this)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        WheelyUtils.logD(TAG, "onConnectionSuspended");
    }

    public void disconnectFusedLocationService() {
        WheelyUtils.logD(TAG, "disconnectFusedLocationService");
        if (mGoogleApiClient != null && (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mGoogleApiClient.disconnect();
        }
        if (mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    private void sendLocationUsingBroadCast(Location location) {
        Intent locationBroadcast = new Intent(LocationService.LOCATION_RECEIVED);
        locationBroadcast.putExtra(LOCATION_RECEIVED_TAG, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(locationBroadcast);
    }

    @Override
    public void onLocationChanged(Location location) {
        synchronized (locking) {

            WheelyUtils.logD(TAG, "Location received successfully [" + location.getLatitude() + "," + location.getLongitude() + "]");
            sendLocationUsingBroadCast(location);
        }
    }

    private void getOldLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                WheelyUtils.logD(TAG, "Location received successfully [" + location.getLatitude() + "," + location.getLongitude() + "]");
                sendLocationUsingBroadCast(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, mLocationListener);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListener);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        WheelyUtils.logD(TAG, "Error connecting to Fused Location Provider");
        getOldLocation();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        WheelyUtils.logD(TAG, "onDestroy");
        super.onDestroy();
        disconnectFusedLocationService();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        WheelyUtils.logD(TAG, "onTaskRemoved");
        disconnectFusedLocationService();
    }
}