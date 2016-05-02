package org.chaynik.wheely.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.chaynik.wheely.model.geo.dto.GeoData;
import org.chaynik.wheely.preferences.Profile;
import org.chaynik.wheely.utils.Const;
import org.chaynik.wheely.utils.WheelyURLBuilder;
import org.chaynik.wheely.utils.WheelyUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";
    public static final String TYPE_MESSAGE = "message";
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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WheelyUtils.logD(TAG, "onStartCommand");
        start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        WheelyUtils.logD(TAG, "onDestroy");
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.disconnect();
        }
        super.onDestroy();
    }

    public void start() {
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
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.sendText(message);
        }
    }

    @Override
    public void onTaskRemoved(Intent Intent) {
        WheelyUtils.logD(TAG, "onTaskRemoved");
    }

    private WebSocketAdapter mWebSocketAdapter = new WebSocketAdapter() {
        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            WheelyUtils.logD(TYPE_MESSAGE, "onConnected!");
            getUserLocationInfo();
//            Log.d(TYPE_MESSAGE, "Connected!");
//            JSONObject json = new JSONObject();
//            try {
//                json.put("lon", 55.755826);
//                json.put("lat", 37.6173);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            websocket.sendText(json.toString());
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            WheelyUtils.logD(TYPE_MESSAGE, "onDisconnected!");
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onError(websocket, cause);
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            WheelyUtils.logD(TYPE_MESSAGE, text);
        }
    };

    private void getUserLocationInfo() {
        if (!WheelyUtils.isServiceRunning(this, LocationService.class)) {
            WheelyUtils.logD(TAG, "LocationService: onCreate");
            startService(new Intent(this, LocationService.class));
        } else {
            WheelyUtils.logD(TAG, "LocationService: onCreated");
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationReceiver, new IntentFilter(LocationService.LOCATION_RECEIVED));
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
//            if (location != null) {
//                if (isNeedAbort) {
//                    context.stopService(new Intent(context, LocationService.class));
//                }
//                LocalBroadcastManager.getInstance(context).unregisterReceiver(mLocationReceiver);
//                setData(location);
//            }

        }
    };


}