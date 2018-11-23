package com.admanager.core;

import android.app.Activity;
import android.util.Log;

import com.admanager.config.RemoteConfigHelper;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdManager {
    final CopyOnWriteArrayList<Boolean> LOADED = new CopyOnWriteArrayList<>();
    final CopyOnWriteArrayList<Boolean> SKIP = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> ENABLE_KEYS = new CopyOnWriteArrayList<>();
    private final ArrayList<Adapter> ADAPTERS = new ArrayList<>();
    String TAG;
    private Listener listener;
    private Activity context;
    private boolean showable = false;
    private boolean stepByStep = false;
    private boolean reload = false;


    public AdManager(Activity activity, String tag) {
        RemoteConfigHelper.init(activity);
        this.context = activity;
        if (tag.equals("")) {
            tag = activity.getClass().getSimpleName();
        }
        this.TAG = "AdManager_" + tag;
        this.TAG = this.TAG.substring(0, Math.min(23, this.TAG.length()));
    }

    public AdManager(Activity activity) {
        this(activity, "");
    }

    Activity getActivity() {
        return this.context;
    }

    public AdManager add(Adapter adapter) {
        Log.d(TAG, "add: " + adapter.getClass().getSimpleName());

        adapter.setOrder(ENABLE_KEYS.size());
        adapter.setManager(this);
        ENABLE_KEYS.add(adapter.getEnableKey());
        LOADED.add(false);
        SKIP.add(false);
        ADAPTERS.add(adapter);
        return this;
    }

    public AdManager build() {
        return build(null);
    }

    public AdManager build(Listener listener) {
        Log.d(TAG, "build: ");
        this.listener = listener;

        if (ENABLE_KEYS.size() != LOADED.size() || LOADED.size() != SKIP.size()) {
            throw new IllegalStateException("WRONG INITIALIZED ADS");
        }

        for (int i = 0; i < LOADED.size(); i++) {
            LOADED.set(i, false);
        }
        for (int i = 0; i < LOADED.size(); i++) {
            SKIP.set(i, false);
        }

        for (Adapter adapter : ADAPTERS) {
            adapter.init();
        }
        return this;
    }

    public void destroy() {
        for (Adapter adapter : ADAPTERS) {
            adapter.destroy();
        }
    }

    public void show() {
        if (ADAPTERS.size() == 0) {
            throw new IllegalStateException("No adapter added");
        }
        this.showable = true;
        display();
    }

    public AdManager stepByStep(boolean reload) {
        this.stepByStep = true;
        this.reload = reload;
        return this;
    }

    synchronized void display() {
        Log.d(TAG, "display()");
        for (int i = 0; i < LOADED.size(); i++) {
            boolean b = LOADED.get(i);
            if (!b) {
                return;
            }
        }

        Log.d(TAG, "display() Loaded");
        if (listener != null) {
            listener.loaded();
        }

        if (!showable) {
            return;
        }

        if (stepByStep) {
            showable = false;
        }

        Log.d(TAG, "display() is displaying");

        for (int i = 0; i < LOADED.size(); i++) {
            if (!SKIP.get(i)) {
                String enableKey = ENABLE_KEYS.get(i);
                Adapter adapter = ADAPTERS.get(i);

                boolean enabled = enableKey == null || RemoteConfigHelper.areAdsEnabled() && RemoteConfigHelper.getConfigs().getBoolean(enableKey);
                Log.d(TAG, "display() enabled=" + enabled + " adapter:" + adapter.getClass().getSimpleName());
                if (enabled) {
                    adapter.show();
                    break;
                } else {
                    SKIP.set(i, true);
                }
            }
        }

        for (int i = 0; i < SKIP.size(); i++) {
            boolean b = SKIP.get(i);
            if (!b) {
                return;
            }
        }

        Log.d(TAG, "display() all displayed");

        if (reload) {
            build(listener);
        }

        if (listener != null) {
            listener.closed();
        }
    }


    public interface Listener {
        void closed();

        void loaded();
    }

}