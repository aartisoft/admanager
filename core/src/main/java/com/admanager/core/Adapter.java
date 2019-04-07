package com.admanager.core;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.util.Log;

public abstract class Adapter {
    private int order = 0;
    private AdManager manager = null;
    private String enableKey;
    private String adapterName;

    public Adapter(String adapterName, String enableKey) {
        this.enableKey = enableKey;
        this.adapterName = adapterName;
    }

    final String getEnableKey() {
        return enableKey;
    }

    final void setOrder(int order) {
        this.order = order;
    }

    final void setManager(AdManager manager) {
        this.manager = manager;
    }

    protected final Activity getActivity() {
        return manager.getActivity();
    }

    protected final boolean isTestMode() {
        return manager.testMode;
    }

    protected final void loaded() {
        Log.d(manager.TAG, getAdapterName() + " loaded");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Adapter.this.manager.setLoaded(order, true);
                Adapter.this.manager.display();

            }
        });
    }

    protected final void error(String error) {
        Log.e(manager.TAG, getAdapterName() + " error :" + error);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Adapter.this.manager.setLoaded(order, false);
                Adapter.this.manager.display();

            }
        });
    }

    protected final void closed() {
        Log.d(manager.TAG, getAdapterName() + " closed");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Adapter.this.manager.setClosed(order);

                Adapter.this.manager.display(true);

            }
        });
    }

    protected final void loge(String message) {
        Log.e(manager.TAG, getAdapterName() + ": " + message);
    }

    protected final void logv(String message) {
        Log.v(manager.TAG, getAdapterName() + ": " + message);
    }

    String getAdapterName() {
        return adapterName;
    }

    protected abstract void show();

    protected abstract void init();

    @CallSuper
    protected void destroy() {

    }

    protected void onCreated() {

    }

    protected void onPause() {

    }

    protected void onResume() {

    }

}
