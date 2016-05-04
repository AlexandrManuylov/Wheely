package org.chaynik.wheely;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.chaynik.wheely.preferences.Profile;

public class LoginFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "LoginFragment";
    public EditText mEditUserName;
    public EditText mEditPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        view.findViewById(R.id.text_sign_in).setOnClickListener(this);
        mEditUserName = (EditText) view.findViewById(R.id.input_user_name);
        mEditPassword = (EditText) view.findViewById(R.id.input_password);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_sign_in:
                String userName = mEditUserName.getText().toString();
                String password = mEditPassword.getText().toString();
                if (userName.isEmpty() || !userName.substring(0, 1).equals("a")) {
                    Toast.makeText(getActivity(), "Incorrect user name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.isEmpty() || !password.substring(0, 1).equals("a")) {
                    Toast.makeText(getActivity(), "Incorrect password", Toast.LENGTH_SHORT).show();
                    return;
                }
                Profile.saveUserInfo(userName, password);
                ((MainActivity) getActivity()).showMapsFragment();
                break;

        }

    }
}
