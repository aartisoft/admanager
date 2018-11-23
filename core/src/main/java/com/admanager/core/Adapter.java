package com.admanager.core;

import android.app.Activity;
import android.util.Log;

public abstract class Adapter {
    private int order = 0;
    private AdManager manager = null;
    private String enableKey;

    public Adapter(String enableKey) {
        this.enableKey = enableKey;
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

    protected final void loaded() {
        Log.d(manager.TAG, getClass().getSimpleName() + " loaded ");
        this.manager.LOADED.set(order, true);
        this.manager.display();
    }

    protected final void error(String error) {
        Log.d(manager.TAG, getClass().getSimpleName() + " error :" + error);
        this.manager.LOADED.set(order, true);
        this.manager.SKIP.set(order, true);

        this.manager.display();
    }

    protected final void closed() {
        Log.d(manager.TAG, getClass().getSimpleName() + " closed ");
        this.manager.SKIP.set(order, true);

        this.manager.display();
    }

    public abstract void show();

    public abstract void init();

    public abstract void destroy();
}
