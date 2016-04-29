package org.chaynik.wheely;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "LoginFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        view.findViewById(R.id.text_sign_in).setOnClickListener(this);
        return view;
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
