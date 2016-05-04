package org.chaynik.wheely.utils;


import org.chaynik.wheely.R;

public enum ModelError {
    LOCATION_PERMISSION_IS_NOT_GRANTED(R.string.model_error_location_permission, R.string.model_error_common_settings_action),
    UNSTABLE_NETWORK_CONNECTION(R.string.model_error_unstable_network_connection, 0),
    CONNECTION_ERROR(R.string.model_error_no_any_network_connection, 0),
    UNKNOWN_ERROR(R.string.model_error_unknown_error, R.string.model_error_unknown_error_action),
    GPS_DISABLED(R.string.model_error_gps_disabled, R.string.model_error_common_settings_action);

    private int mTextId;
    private int mTextActionId;

    ModelError(int textId, int textActionId) {
        mTextId = textId;
        mTextActionId = textActionId;
    }

    public int getTextId() {
        return mTextId;
    }

    public int getTextActionId() {
        return mTextActionId;
    }
}
