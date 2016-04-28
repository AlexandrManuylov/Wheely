package org.chaynik.wheely.service;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class WebSocketService extends Service {
    private boolean isActive;
    private Handler mHandler = new Handler();

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WebSocket ws = new WebSocketFactory().createSocket("ws://mini-mdt.wheely.com/?username=a&password=a");
                    ws.addListener(new SocketListener());
                    ws.setPingInterval(60 * 1000);
                    ws.connect();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        super.onDestroy();
        isActive = false;
        mHandler.removeCallbacks(mUpdateTimeTask);
        Log.i("Test", "Service: onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("Test", "Service: onTaskRemoved");
    }

}