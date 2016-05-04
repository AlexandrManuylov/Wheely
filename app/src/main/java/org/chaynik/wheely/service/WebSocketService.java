package org.chaynik.wheely.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketError;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.chaynik.wheely.model.geo.dto.GeoData;
import org.chaynik.wheely.preferences.Profile;
import org.chaynik.wheely.utils.Const;
import org.chaynik.wheely.utils.WheelyURLBuilder;
import org.chaynik.wheely.utils.WheelyUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class WebSocketService extends Service {
    public final static String GEO_INFO_RECEIVED = "geo.info.received";
    public final static String GEO_INFO_RECEIVED_TAG = "info_received";
    private static final String TAG = "WebSocketService";
    public static final String TYPE_MESSAGE = "message";
    private Handler mHandler = new Handler();
    private WebSocket mWebSocket;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        WheelyUtils.logD(TAG, "onCreate");
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        Notification notification = builder.build();
//        startForeground(777, notification);
//        Intent hideIntent = new Intent(this, HideService.class);
//        startService(hideIntent);

    }

    private Runnable mTryInternetConnection = new Runnable() {
        public void run() {
            WheelyUtils.logD(TAG, "mTryInternetConnection");
            start();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WheelyUtils.logD(TAG, "onStartCommand");
        start();
        return START_STICKY;
    }

    private void start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !WheelyUtils.isLocationPermissionGranted(this)) {
            stopSelf();
        } else {
            initWebSocket();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WheelyUtils.logD(TAG, "onDestroy");
        shutDownLocationService();
    }

    public void initWebSocket() {
        WheelyUtils.logD(TAG, "initWebSocket");
        try {
            Map<String, String> params = new ArrayMap<>();
            params.put(Const.USER_NAME_PARAM, Profile.getUserName());
            params.put(Const.USER_PASSWORD_PARAM, Profile.getUserPassword());
            URL url = WheelyURLBuilder.createURL(params);
            mWebSocket = new WebSocketFactory().createSocket(url);
            mWebSocket.addListener(mWebSocketAdapter);
            mWebSocket.connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendMessage(String message) {
        if (mWebSocket != null) {
            if (mWebSocket.isOpen()) {
                mWebSocket.sendText(message);
            } else {
                initWebSocket();
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent Intent) {
        shutDownLocationService();
        WheelyUtils.logD(TAG, "onTaskRemoved");
    }

    public void shutDownLocationService() {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.disconnect();
        }
        stopLocationService();
    }

    private void stopLocationService() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver);
        stopService(new Intent(this, LocationService.class));
    }

    private WebSocketAdapter mWebSocketAdapter = new WebSocketAdapter() {
        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            getUserLocationInfo();
            WheelyUtils.logD(TYPE_MESSAGE, "onConnected!");

        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            WheelyUtils.logD(TYPE_MESSAGE, "onDisconnected!");
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            if (cause != null && cause.getError() == WebSocketError.SOCKET_CONNECT_ERROR) {
                if (WheelyUtils.isServiceRunning(WebSocketService.this, LocationService.class)) {
                    stopLocationService();
                }
                mHandler.postDelayed(mTryInternetConnection, 5000);
            }
            WheelyUtils.logD(TYPE_MESSAGE, cause.getError().name());
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            WheelyUtils.logD(TAG, "onTextMessage: - " + text);
            Intent locationBroadcast = new Intent(GEO_INFO_RECEIVED);
            locationBroadcast.putExtra(GEO_INFO_RECEIVED_TAG, text);
            LocalBroadcastManager.getInstance(WebSocketService.this).sendBroadcast(locationBroadcast);

        }
    };

    private void getUserLocationInfo() {
        if (!WheelyUtils.isServiceRunning(this, LocationService.class)) {
            WheelyUtils.logD(TAG, "LocationService: onCreate");
            startService(new Intent(this, LocationService.class));
            LocalBroadcastManager.getInstance(this).registerReceiver(mLocationReceiver, new IntentFilter(LocationService.LOCATION_RECEIVED));
        } else {
            WheelyUtils.logD(TAG, "LocationService: onCreated");
        }

    }

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                Location location = (Location) intent.getExtras().get(LocationService.LOCATION_RECEIVED_TAG);
                if (location != null) {
                    GeoData geoData = new GeoData(location.getLatitude(), location.getLongitude());
                    String message = WheelyUtils.objectToJson(geoData);
                    WheelyUtils.logD(TAG, "mLocationReceiver: sendMessage - " + message);
                    sendMessage(message);
                }
            }
        }
    };

}