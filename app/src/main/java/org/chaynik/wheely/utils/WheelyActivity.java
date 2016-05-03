package org.chaynik.wheely.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;

import org.chaynik.wheely.WheelyApp;
import org.chaynik.wheely.model.Model;

import java.util.ArrayList;
import java.util.List;

public class WheelyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private List<Pair<ModelBase, ModelBase.Listener>> mModelListeners = new ArrayList<Pair<ModelBase, ModelBase.Listener>>();

    public void registerModelListener(ModelBase model, ModelBase.Listener listener) {
        mModelListeners.add(new Pair<ModelBase, ModelBase.Listener>(model, listener));
    }

    public void unregisterAllModelListeners() {
        mModelListeners.clear();
    }

    private void addModelListeners() {
        for (Pair<ModelBase, ModelBase.Listener> pair : mModelListeners) {
            pair.first.addListener(pair.second);
        }
    }

    protected void removeModelsListeners() {
        for (Pair<ModelBase, ModelBase.Listener> pair : mModelListeners) {
            pair.first.removeListener(pair.second, !isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        addModelListeners();
    }

    public WheelyApp getApp() {
        return WheelyApp.getInstance();
    }

    public Model getModel() {
        return getApp().getModel();
    }

}
