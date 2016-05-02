package org.chaynik.wheely.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.chaynik.wheely.utils.WheelyUtils;


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
            Log.d(TAG, "Start mLocationClient");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        } else {
            Log.d(TAG, "Start getOldLocation()");
            getOldLocation();
        }
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void disconnectFusedLocationService() {
        WheelyUtils.logD(TAG, "disconnectFusedLocationService");
        if (mGoogleApiClient != null && (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mGoogleApiClient.disconnect();
        }
        if (mLocationListener != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
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
            Log.d(TAG, "Location received successfully [" + location.getLatitude() + "," + location.getLongitude() + "]");
            sendLocationUsingBroadCast(location);
        }
    }

    private void getOldLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location received successfully [" + location.getLatitude() + "," + location.getLongitude() + "]");
                sendLocationUsingBroadCast(location);
                if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocationManager.removeUpdates(mLocationListener);
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
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Error connecting to Fused Location Provider");
        getOldLocation();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        disconnectFusedLocationService();
    }
}