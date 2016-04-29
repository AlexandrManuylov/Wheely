package org.chaynik.wheely.service;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.chaynik.wheely.preferences.Profile;
import org.chaynik.wheely.utils.Const;
import org.chaynik.wheely.utils.WheelyURLBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class WebSocketService extends Service {
    final String BASE_URL = "http://api.example.org/data/2.5/forecast/daily?";
    final String USER_NAME_PARAM = "username";
    final String USER_PASSWORD_PARAM = "password";

    private boolean isActive;
    private Handler mHandler = new Handler();
    private WebSocket mWebSocket;

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Log.i("task", "spam");
            if (isActive) {
                mHandler.postDelayed(this, 1000);
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Test", "Service: onCreate");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder.build();
        startForeground(777, notification);
        Intent hideIntent = new Intent(this, HideService.class);
        startService(hideIntent);

    }

    public void start() {
//        try {
//            Map<String, String> params = new ArrayMap<>();
//            params.put(Const.USER_NAME_PARAM, Profile.getUserName());
//            params.put(Const.USER_PASSWORD_PARAM, Profile.getUserPassword());
//            URL url = WheelyURLBuilder.createURL(params);
//            mWebSocket = new WebSocketFactory().createSocket(url);
//            mWebSocket.addListener(new SocketListener());
//            mWebSocket.setPingInterval(60 * 1000);
//            mWebSocket.connectAsynchronously();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Test", "Service: onStartCommand");
        isActive = true;
        mHandler.postDelayed(mUpdateTimeTask, 1000);
        start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("Test", "Service: onDestroy");

        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.disconnect();
        }
        isActive = false;
        mHandler.removeCallbacks(mUpdateTimeTask);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("Test", "Service: onTaskRemoved");
    }

}