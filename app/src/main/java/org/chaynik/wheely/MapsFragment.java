package org.chaynik.wheely;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import org.chaynik.wheely.service.HideService;
import org.chaynik.wheely.service.WebSocketService;
import org.chaynik.wheely.utils.WheelyUtils;

public class MapsFragment extends Fragment implements MenuItem.OnMenuItemClickListener, OnRequestPermissionsResultCallback, View.OnClickListener {
    public static final String TAG = "MapsFragment";
    public static final int REQUEST_LOCATION = 7;
    public static final String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.maps_fragment, container, false);
        view.findViewById(R.id.text_show_settings).setOnClickListener(this);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPermissionGranted()) {
            if (isNeedShowRequestAlert()) {
                showRequestAlert(LOCATION_PERMISSION, REQUEST_LOCATION);
            } else {
                WheelyUtils.logD("shouldShowRequestPermissionRationale", "beda!!!");
            }
        } else {
            startWebSocketService();
        }
    }

    private void startWebSocketService() {
        if (!WheelyUtils.isServiceRunning(getActivity(), WebSocketService.class)) {
            Log.i("Test", "Activity: onCreate");
            getActivity().startService(new Intent(getActivity(), WebSocketService.class));
        } else {
            Log.i("Test", "Activity: onCreated");
        }
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private boolean isNeedShowRequestAlert() {

        return shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);

    }

    private void showRequestAlert(String[] permissions, int requestCode) {
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (permissions.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startWebSocketService();
            } else {
                if (isNeedShowRequestAlert()) {
                    showRequestAlert(permissions, requestCode);
                } else {
                    WheelyUtils.logD("shouldShowRequestPermissionRationale", "beda!!!");
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_sign_out).setOnMenuItemClickListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.i("Test", "Activity: onStop");
        getActivity().stopService(new Intent(getActivity(), WebSocketService.class));
        ((MainActivity) getActivity()).showLoginFragment();
        return false;
    }

    @Override
    public void onClick(View v) {
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri), REQUEST_LOCATION);
    }
}
