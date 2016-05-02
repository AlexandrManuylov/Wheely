package org.chaynik.wheely.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ModelBase {
    private Set<Listener> mListeners = new LinkedHashSet<>();
    private Map<Class, Boolean> mListenerCache = new HashMap<>();
    protected boolean mIsUpdating = false;


    public boolean isUpdating() {
        return mIsUpdating;
    }

    public List<Listener> getListeners() {
        return new ArrayList<>(mListeners);
    }

    protected void stopRequest() {
        mIsUpdating = false;
        notifyListeners();
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
        final Boolean stateChanged = mListenerCache.remove(listener.getClass());
        if (Boolean.TRUE.equals(stateChanged)) {
            listener.onStateChanged();
        }
    }

    public void removeListener(Listener listener, boolean temporarily) {
        if (mListeners.remove(listener)) {
            if (temporarily) {
                mListenerCache.put(listener.getClass(), false);
            } else {
                mListenerCache.remove(listener.getClass());
            }
        }
    }

    protected void notifyListeners() {
        for (Listener l : getListeners()) {
            l.onStateChanged();
        }
        for (Class c : mListenerCache.keySet()) {
            mListenerCache.put(c, true);
        }
    }

    protected void errorNotifyListeners() {

        for (Listener l : getListeners()) {
            l.onError();
        }
        for (Class c : mListenerCache.keySet()) {
            mListenerCache.put(c, true);
        }
    }

     public interface Listener {
        void onStateChanged();
        void onError();
    }
}
