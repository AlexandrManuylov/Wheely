package org.chaynik.wheely;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.chaynik.wheely.model.geo.GeoInfo;
import org.chaynik.wheely.preferences.Preferences;
import org.chaynik.wheely.service.WebSocketService;
import org.chaynik.wheely.utils.ModelBase;
import org.chaynik.wheely.utils.ModelError;
import org.chaynik.wheely.utils.WheelyFragment;
import org.chaynik.wheely.utils.WheelyUtils;

public class MapsFragment extends WheelyFragment implements MenuItem.OnMenuItemClickListener, OnRequestPermissionsResultCallback, View.OnClickListener, OnMapReadyCallback {
    public static final String TAG = "MapsFragment";
    public static final String CONTENT_MAP_STATE = "map_view_state";
    public static final int REQUEST_LOCATION = 7;
    public static final String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private GeoInfo mGeoInfo;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Snackbar mSnackBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.maps_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.map_view);
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle(CONTENT_MAP_STATE) : null;
        mMapView.onCreate(mapViewSavedInstanceState);
//        view.findViewById(R.id.text_show_settings).setOnClickListener(this);
        setHasOptionsMenu(true);
        mGeoInfo = getModel().geoInfo;
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view;
        mSnackBar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE);


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        final Bundle mapViewSaveState = new Bundle(outState);
        mMapView.onSaveInstanceState(mapViewSaveState);
        outState.putBundle(CONTENT_MAP_STATE, mapViewSaveState);
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onResume() {
        registerModelListener(mGeoInfo, mGeoListener);
        super.onResume();
        mMapView.onResume();
        if (!WheelyUtils.isLocationPermissionGranted(getActivity())) {
            if (isNeedShowRequestAlert()) {
                showRequestAlert(LOCATION_PERMISSION, REQUEST_LOCATION);
            } else {
                showSnackError();
                stopWebSocketService();
            }
        } else {
            startWebSocketService();
            mGeoInfo.registerGeoReceiver();
            if (mGoogleMap != null){
                mGoogleMap.setMyLocationEnabled(true);
            }

        }
    }

    private void stopWebSocketService() {
        getActivity().stopService(new Intent(getActivity(), WebSocketService.class));
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mGeoInfo.removeListener(mGeoListener, false);
        mGeoInfo.unRegisterGeoReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void startWebSocketService() {
        if (!WheelyUtils.isServiceRunning(getActivity(), WebSocketService.class)) {
            Log.i("Test", "Activity: onCreate");
            getActivity().startService(new Intent(getActivity(), WebSocketService.class));
        } else {
            Log.i("Test", "Activity: onCreated");
        }
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
                    showSnackError();
                    stopWebSocketService();
                }
            }
        }
    }

    private void showSnackError() {
        mSnackBar = getSnackBarByError(ModelError.LOCATION_PERMISSION_IS_NOT_GRANTED, Snackbar.LENGTH_INDEFINITE);
        mSnackBar.show();
    }

    @Override
    public void snackActionClick(View v) {
        super.snackActionClick(v);
        switch (Integer.parseInt(mSnackBar.getView().getTag().toString())) {
            case R.string.model_error_location_permission_action:
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri), REQUEST_LOCATION);
                break;

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
        Preferences.clearAllPreferences();
        ((MainActivity) getActivity()).showLoginFragment();
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    private ModelBase.Listener mGeoListener = new ModelBase.Listener() {
        @Override
        public void onStateChanged() {
            WheelyUtils.logD(TAG, "onStateChanged");
        }

        @Override
        public void onError() {

        }
    };



    private boolean isNeedShowRequestAlert() {
        return shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);

    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(55.79040079, 37.38059521)).zoom(12).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (WheelyUtils.isLocationPermissionGranted(getActivity())) {
            mGoogleMap.setMyLocationEnabled(true);
        }
    }
}
