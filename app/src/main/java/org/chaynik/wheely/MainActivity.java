package org.chaynik.wheely;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.chaynik.wheely.service.WebSocketService;
import org.chaynik.wheely.utils.WheelyUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_start_service).setOnClickListener(this);
        findViewById(R.id.button_stop_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_service:
                if (!WheelyUtils.isServiceRunning(this, WebSocketService.class)) {
                    Log.i("Test", "Activity: onCreate");
                    startService(new Intent(this, WebSocketService.class));
                } else {
                    Log.i("Test", "Activity: onCreated");
                }
                break;
            case R.id.button_stop_service:
                Log.i("Test", "Activity: onStop");
                stopService(new Intent(this, WebSocketService.class));
                break;
        }

    }
}
