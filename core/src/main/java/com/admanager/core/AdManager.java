package com.admanager.core;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;

import com.admanager.config.RemoteConfigHelper;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdManager {
    private static int globalCounter = 1;
    private final CopyOnWriteArrayList<Boolean> LOADED = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Boolean> SKIP = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> ENABLE_KEYS = new CopyOnWriteArrayList<>();
    private final ArrayList<Adapter> ADAPTERS = new ArrayList<>();
    String TAG;
    boolean testMode;
    private boolean showable = false;
    private Listener listener;
    private Activity context;
    private final Application.ActivityLifecycleCallbacks LIFECYCLE_CALLBACKS = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            if (isActivityEquals(activity)) {
                onCreated();
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }


        @Override
        public void onActivityResumed(Activity activity) {
            if (isActivityEquals(activity)) {
                onResume();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (isActivityEquals(activity)) {
                onPause();
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (isActivityEquals(activity)) {
                destroy();
                context.getApplication().unregisterActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
            }
        }
    };
    private boolean stepByStep = false;
    private Intent intent;
    private View clickView;
    private boolean showAndFinish;
    private long timeCap;
    private long lastTimeShowed;

    AdManager(Activity activity) {
        RemoteConfigHelper.init(activity);
        this.context = activity;
        String tag = activity.getClass().getSimpleName();
        this.TAG = "AdManager_" + (globalCounter++) + "_" + tag;
        this.TAG = this.TAG.substring(0, Math.min(23, this.TAG.length()));

        context.getApplication().registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
    }

    public static String arrayToString(CopyOnWriteArrayList<?> list) {
        if (list == null) {
            return "null";
        }
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o == null) {
                ret.append("null,");
            } else {
                ret.append(o.toString())
                        .append(",");
            }
        }
        return ret.toString();
    }

    Activity getActivity() {
        return this.context;
    }

    void setIntent(Intent intent) {
        this.intent = intent;
    }

    private void setClickView(View clickView) {
        this.clickView = clickView;
    }

    AdManager add(Adapter adapter) {
        adapter.setOrder(ENABLE_KEYS.size());
        adapter.setManager(this);
        ENABLE_KEYS.add(adapter.getEnableKey());
        LOADED.add(false);
        SKIP.add(false);
        ADAPTERS.add(adapter);
        return this;
    }

    AdManager build(Listener listener, boolean testMode) {
        Log.d(TAG, "initializing");
        this.listener = listener;
        this.testMode = testMode;

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

    private void onPause() {
        Log.d(TAG, "pause");
        for (Adapter adapter : ADAPTERS) {
            adapter.onPause();
        }
    }

    private void onCreated() {
        Log.d(TAG, "onCreated");
        for (Adapter adapter : ADAPTERS) {
            adapter.onCreated();
        }
    }

    private void onResume() {
        Log.d(TAG, "resume");
        for (Adapter adapter : ADAPTERS) {
            adapter.onResume();
        }
    }

    private void destroy() {
        Log.d(TAG, "Destroying");
        for (int i = 0; i < SKIP.size(); i++) {
            this.LOADED.set(i, true);
            this.SKIP.set(i, true);
        }
        for (Adapter adapter : ADAPTERS) {
            adapter.destroy();
        }
    }

    public void show() {
        show(false);
    }

    private void show(boolean stepByStep) {
        this.stepByStep = stepByStep;
        if (ADAPTERS.size() == 0) {
            throw new IllegalStateException("No adapter added");
        }
        this.showable = true;
        display();
    }

    public void showOneByTimeCap(long timeCap) {
        this.timeCap = timeCap;
        showOne();
    }

    public void showAndFinish() {
        if (this.intent != null) {
            throw new IllegalStateException("Not allowed to use 'thenStart(...)' and 'showAndFinish()' together.");
        }
        this.showAndFinish = true;
        show();
    }

    public void showOne() {
        show(true);
    }

    public void showOnClick(@IdRes int viewId) {
        showOnClick(viewId, false);
    }

    public void showOnClick(@IdRes int viewId, boolean hideViewWhenLoading) {
        final View view = getActivity().findViewById(viewId);
        if (hideViewWhenLoading) {
            view.setVisibility(View.INVISIBLE);
            setClickView(view);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
    }

    synchronized void display() {
        display(false);
    }

    synchronized void display(boolean closedRecently) {
        if (Utils.anyFalse(LOADED)) {
            // Wait until all ads are loaded
            return;
        }

        if (closedRecently) {
            // reload only after ad closed
            reload();
        }

        if (!showable) {
            // 'show..' method not called yet. Don't show ads and let user to call 'show..'
            return;
        }

        if (checkTimeCap()) {
            return;
        }

        if (stepByStep) {
            // if stepbystep mode on, wait another 'show..' call to continue next ad.
            showable = false;
        }

        for (int i = 0; i < LOADED.size(); i++) {
            if (!SKIP.get(i)) {
                String enableKey = ENABLE_KEYS.get(i);
                Adapter adapter = ADAPTERS.get(i);

                boolean enabledNotNull = enableKey != null;
                boolean areAdsEnabled = RemoteConfigHelper.areAdsEnabled();
                boolean remoteConfigEnabled = RemoteConfigHelper.getConfigs().getBoolean(enableKey);
                boolean enabled = enabledNotNull && areAdsEnabled && remoteConfigEnabled;

                Log.v(TAG, " enableKey:" + enableKey);
                Log.v(TAG, " enabledNotNull:" + enabledNotNull + " areAdsEnabled:" + areAdsEnabled + " remoteConfigEnabled:" + remoteConfigEnabled + " enabled:" + enabled);
                Log.v(TAG, " LOADED size:" + LOADED.size() + " list:" + arrayToString(LOADED));
                Log.v(TAG, " SKIP size:" + SKIP.size() + " list:" + arrayToString(SKIP));
                Log.v(TAG, " ENABLE_KEYS size:" + ENABLE_KEYS.size() + " list:" + arrayToString(ENABLE_KEYS));
                if (enabled || testMode) {
                    Log.d(TAG, "Displaying " + adapter.getClass().getSimpleName());
                    adapter.show();
                    lastTimeShowed = System.currentTimeMillis();
                    this.showable = false; // stop showing new ads
                    break;
                } else {
                    Log.d(TAG, adapter.getClass().getSimpleName() + " not enabled");
                    SKIP.set(i, true);

                    if (stepByStep) {
                        break;
                    }
                }
            }
        }

        if (Utils.anyFalse(SKIP)) {
            return;
        }

        // reload if error occurred
        reload();

        startNextActivity();
    }

    private void setClickViewVisible() {
        if (clickView != null) {
            clickView.setVisibility(View.VISIBLE);
            clickView = null;
        }
    }

    private void startNextActivity() {
        if (intent != null) {
            Log.d(TAG, "Starting Next Activity");
            getActivity().startActivity(intent);
            getActivity().finish();
        } else {
            if (showAndFinish) {
                getActivity().finish();
            }
        }
    }

    private void reload() {
        if (stepByStep && Utils.allTrue(SKIP)) {
            Log.d(TAG, "Reloading ads");
            destroy();
            build(listener, testMode);
            this.showable = false;
        }
    }

    private boolean checkTimeCap() {
        if (timeCap > 0 && lastTimeShowed == 0) {
            lastTimeShowed = System.currentTimeMillis();
            Log.d(TAG, "Ad will not be displayed for first call due to the time cap set.");
            return true;
        }
        long now = System.currentTimeMillis();
        long timePassed = now - lastTimeShowed;
        if (timeCap > 0 && timePassed < timeCap) {
            Log.w(TAG, "Not enough time passed after last display. You should wait " + (timeCap - timePassed) + "ms to show.");
            return true;
        }
        return false;
    }

    private boolean isActivityEquals(Activity activity) {
        return activity != null && activity.getClass().getName().equals(getActivity().getClass().getName());
    }

    void setLoaded(int order) {
        this.LOADED.set(order, true);
        if (Utils.allTrue(LOADED)) {
            Log.d(TAG, "loaded all");
            if (listener != null) {
                listener.loaded();
            }
            setClickViewVisible();
        }
    }

    void setSkip(int order) {
        this.SKIP.set(order, true);
        if (Utils.allTrue(SKIP)) {
            Log.d(TAG, "closed all");
            if (listener != null) {
                listener.closed();
            }
        }

    }

    void setClosed(int order) {
        this.setSkip(order);
        if (!this.stepByStep) {
            this.showable = true; // enable to display new ads
        }
        this.lastTimeShowed = System.currentTimeMillis();
    }

    public interface Listener {
        void closed();

        void loaded();
    }

}