package org.chaynik.wheely;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.chaynik.wheely.model.geo.GeoInfo;
import org.chaynik.wheely.model.geo.dto.GeoData;
import org.chaynik.wheely.preferences.Preferences;
import org.chaynik.wheely.service.WebSocketService;
import org.chaynik.wheely.utils.ModelBase;
import org.chaynik.wheely.utils.ModelError;
import org.chaynik.wheely.utils.WheelyFragment;
import org.chaynik.wheely.utils.WheelyUtils;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends WheelyFragment implements MenuItem.OnMenuItemClickListener, OnRequestPermissionsResultCallback, OnMapReadyCallback {
    public static final String TAG = "MapsFragment";
    public static final String CONTENT_MAP_STATE = "map_view_state";
    public static final int REQUEST_LOCATION = 7;
    public static final String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private GeoInfo mGeoInfo;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Snackbar mSnackBar;
    private BitmapDescriptor mMarkerIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.maps_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.map_view);
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle(CONTENT_MAP_STATE) : null;
        mMapView.onCreate(mapViewSavedInstanceState);
        setHasOptionsMenu(true);
        mGeoInfo = getModel().geoInfo;
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
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
                showPermissionSnackError();
                stopWebSocketService();
            }
        } else {
            startWebSocketService();
            mGeoInfo.registerGeoReceiver();
            if (mGoogleMap != null) {
                mGoogleMap.setMyLocationEnabled(true);
            }
            if (WheelyUtils.isGeoDisabled()){
                mGeoInfo.setError(ModelError.GPS_DISABLED);
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
            getActivity().startService(new Intent(getActivity(), WebSocketService.class));
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
                    showPermissionSnackError();
                    stopWebSocketService();
                }
            }
        }
    }

    private void showPermissionSnackError() {
        mSnackBar = getSnackBarByError(ModelError.LOCATION_PERMISSION_IS_NOT_GRANTED, Snackbar.LENGTH_INDEFINITE);
        mSnackBar.show();
    }

    @Override
    public void snackActionClick(View v) {
        super.snackActionClick(v);
        switch (Integer.parseInt(mSnackBar.getView().getTag().toString())) {
            case R.string.model_error_location_permission:
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri), REQUEST_LOCATION);
                break;
            case R.string.model_error_gps_disabled:
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
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
        getActivity().stopService(new Intent(getActivity(), WebSocketService.class));
        Preferences.clearAllPreferences();
        ((MainActivity) getActivity()).showLoginFragment();
        return false;
    }

    private ModelBase.Listener mGeoListener = new ModelBase.Listener() {
        @Override
        public void onStateChanged() {
            if (mSnackBar != null && mSnackBar.isShown() && mGeoInfo.getError() == null) {
                mSnackBar.dismiss();
            }
            if (mGoogleMap != null) {
                List<GeoData> geoDataList = mGeoInfo.getData();
                if (geoDataList != null && geoDataList.size() > 0) {
                    List<Marker> markers = new ArrayList<>();
                    mGoogleMap.clear();
                    for (GeoData data : geoDataList) {
                        MarkerOptions marker = new MarkerOptions()
                                .position(new LatLng(data.getLat(), data.getLon()))
                                .icon(mMarkerIcon);
                        Marker addedMarker = mGoogleMap.addMarker(marker);
                        markers.add(addedMarker);

                    }
                    LatLngBounds.Builder b = new LatLngBounds.Builder();
                    for (Marker m : markers) {
                        b.include(m.getPosition());
                    }
                    LatLngBounds bounds = b.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, WheelyUtils.dpToPx(56));
                    mGoogleMap.animateCamera(cu);
                }
            }
        }

        @Override
        public void onError() {
            ModelError error = mGeoInfo.getError();
            if (mSnackBar == null || (!mSnackBar.isShown() && Integer.parseInt(mSnackBar.getView().getTag().toString()) != error.getTextId())) {
                mSnackBar = getSnackBarByError(error, Snackbar.LENGTH_INDEFINITE);
                mSnackBar.show();
            }
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
        if (WheelyUtils.isLocationPermissionGranted(getActivity())) {
            mGoogleMap.setMyLocationEnabled(true);
        }
        mMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
    }
}
