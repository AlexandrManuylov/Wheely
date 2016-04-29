package org.chaynik.wheely;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class MapsFragment extends Fragment implements MenuItem.OnMenuItemClickListener {
    public static final String TAG = "MapsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.maps_fragment, container, false);
        setHasOptionsMenu(true);
//        if (!WheelyUtils.isServiceRunning(getActivity(), WebSocketService.class)) {
//            Log.i("Test", "Activity: onCreate");
//            getActivity().startService(new Intent(getActivity(), WebSocketService.class));
//        } else {
//            Log.i("Test", "Activity: onCreated");
//        }
        return view;
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
}
