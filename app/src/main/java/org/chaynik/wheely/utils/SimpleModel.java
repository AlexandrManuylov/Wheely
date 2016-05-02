package org.chaynik.wheely.utils;


public abstract class SimpleModel<DataType> extends ModelBase {
    protected DataType mData;


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
        mData = data;
        notifyListeners();
    }

    public void setError() {
        mIsUpdating = false;
        errorNotifyListeners();
    }

}
