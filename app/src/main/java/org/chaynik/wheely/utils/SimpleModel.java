package org.chaynik.wheely.utils;


public abstract class SimpleModel<DataType> extends ModelBase {
    protected DataType mData;
    protected ModelError mModelError;


    public SimpleModel() {
        super();
    }

    public void startNewRequest() {
        mIsUpdating = true;
        notifyListeners();
    }

    public DataType getData() {
        return mData;
    }

    public void setData(final DataType data) {
        mIsUpdating = false;
        mModelError = null;
        mData = data;
        notifyListeners();
    }

    public void setError(ModelError error) {
        mModelError = error;
        mIsUpdating = false;
        errorNotifyListeners();
    }

    public ModelError getError() {
        return mModelError;
    }
}
