package com.admanager.core;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.IntRange;
import android.util.Log;

public abstract class Adapter {
    private int order = 0;
    private AdManager manager = null;
    private String enableKey;
    private String adapterName;
    private int timeout;
    private Handler handler;
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            loge("timeout");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Adapter.this.manager.setLoaded(order, true);
                    Adapter.this.manager.display();

                }
            });
        }
    };

    public Adapter(String adapterName, String enableKey) {
        this.enableKey = enableKey;
        this.adapterName = adapterName;
        withTimeout(15_000);
    }

    public Adapter withTimeout(@IntRange(from = 0, to = 120_000) int timeout) {
        if (timeout > 0 && timeout < 3000) {
            Log.w("ADM", getAdapterName() + ": You should pass timeout in milliseconds. AdManager recommend timeout longer than 3000 ms.");
        }
        this.timeout = timeout;
        return this;
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
        stopTimer();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Adapter.this.manager.setLoaded(order, true);
                Adapter.this.manager.display();

            }
        });
    }

    protected final void clicked() {
        clicked(null);
    }

    protected final void clicked(final String tag) {
        logv("clicked");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Adapter.this.manager.clicked(order, getAdapterName(), tag);
            }
        });
    }

    protected final void error(String error) {
        Log.e(manager.TAG, getAdapterName() + " error :" + error);
        stopTimer();
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
        stopTimer();
    }

    protected void onCreated() {

    }

    protected void onPause() {

    }

    protected void onResume() {

    }

    private void stopTimer() {
        if (handler != null && r != null) {
            handler.removeCallbacks(r);
            handler = null;
        }
    }

    void startTimer() {
        if (timeout <= 0) {
            return;
        }
        handler = new Handler();
        handler.postDelayed(r, timeout);
    }
}
