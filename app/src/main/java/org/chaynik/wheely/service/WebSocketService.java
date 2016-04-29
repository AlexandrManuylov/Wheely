package org.chaynik.wheely.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.support.v4.util.ArrayMap;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.chaynik.wheely.preferences.Profile;
import org.chaynik.wheely.utils.Const;
import org.chaynik.wheely.utils.WheelyURLBuilder;
import org.chaynik.wheely.utils.WheelyUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";
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

    public void start() {
        try {
            Map<String, String> params = new ArrayMap<>();
            params.put(Const.USER_NAME_PARAM, Profile.getUserName());
            params.put(Const.USER_PASSWORD_PARAM, Profile.getUserPassword());
            URL url = WheelyURLBuilder.createURL(params);
            mWebSocket = new WebSocketFactory().createSocket(url);
            mWebSocket.addListener(new SocketListener());
            mWebSocket.connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        WheelyUtils.logD(TAG, "onTaskRemoved");
    }

}