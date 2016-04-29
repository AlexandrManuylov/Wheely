package org.chaynik.wheely;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String mSelectedTag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragmentByTag(LoginFragment.TAG);
//        findViewById(R.id.button_start_service).setOnClickListener(this);
//        findViewById(R.id.button_stop_service).setOnClickListener(this);
    }

    private void showFragmentByTag(String tag) {
        String oldTag = mSelectedTag;
        mSelectedTag = tag;
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment oldFragment = fm.findFragmentByTag(oldTag);
        final Fragment fragment = fm.findFragmentByTag(tag);

        if (oldFragment != null && !tag.equals(oldTag)) {
            ft.detach(oldFragment);
        }

        if (fragment == null) {
            ft.replace(R.id.container, getContentFragment(tag), tag);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();

    }

    private Fragment getContentFragment(String tag) {
        Fragment fragment = null;
        if (LoginFragment.TAG.equals(tag)) {
            fragment = new LoginFragment();
        } else if (MapsFragment.TAG.equals(tag)) {
            fragment = new MapsFragment();
        }
        return fragment;
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.button_start_service:
//                if (!WheelyUtils.isServiceRunning(this, WebSocketService.class)) {
//                    Log.i("Test", "Activity: onCreate");
//                    startService(new Intent(this, WebSocketService.class));
//                } else {
//                    Log.i("Test", "Activity: onCreated");
//                }
//                break;
//            case R.id.button_stop_service:
//                Log.i("Test", "Activity: onStop");
//                stopService(new Intent(this, WebSocketService.class));
//                break;
//        }

    }
}
