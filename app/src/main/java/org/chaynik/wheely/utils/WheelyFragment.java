package org.chaynik.wheely.utils;


import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;

import org.chaynik.wheely.R;
import org.chaynik.wheely.WheelyApp;
import org.chaynik.wheely.model.Model;

import java.util.ArrayList;
import java.util.List;


public class WheelyFragment extends Fragment implements SnackActionListener{
    private List<Pair<ModelBase, ModelBase.Listener>> mModelListeners = new ArrayList<Pair<ModelBase, ModelBase.Listener>>();

    @Override
    public void onResume() {
        super.onResume();
        addModelListeners();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void registerModelListener(ModelBase model, ModelBase.Listener listener) {
        mModelListeners.add(new Pair<>(model, listener));
    }

    private void addModelListeners() {
        for (Pair<ModelBase, ModelBase.Listener> pair : mModelListeners) {
            pair.first.addListener(pair.second);
        }
    }

    protected void removeModelsListeners() {
        for (Pair<ModelBase, ModelBase.Listener> pair : mModelListeners) {
            pair.first.removeListener(pair.second, !isRemoving());
        }
    }


    public Model getModel() {
        return getApp().getModel();
    }

    public WheelyApp getApp() {
        return WheelyApp.getInstance();
    }

    public Snackbar getSnackBarByError(ModelError error, @Snackbar.Duration int duration) {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getView();
        Snackbar snackbar = Snackbar.make(coordinatorLayout, error.getTextId(), duration);
        if (error.getTextActionId() > 0) {
            snackbar.setActionTextColor(getResources().getColor(R.color.red));
            snackbar.getView().setTag(error.getTextActionId());
            snackbar.setAction(error.getTextActionId(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackActionClick(v);
                }
            });
        }
        return snackbar;
    }



    @Override
    public void snackActionClick(View v) {

    }
}
