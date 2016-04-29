package org.chaynik.wheely.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class HideService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder.build();
        startForeground(777, notification);
        stopForeground(true);
    }

    @Override
    public void onDestroy() {

        Log.i("Test", "HideServicea: onDestroy");
        super.onDestroy();
    }
}
