package com.admanager.core;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.admanager.config.RemoteConfigHelper;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdManager {
    private static int globalCounter = 1;
    private final CopyOnWriteArrayList<Boolean> LOADED = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Boolean> SKIP = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> ENABLE_KEYS = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Boolean> ADAPTER_FINISH_LISTENER_CALLED = new CopyOnWriteArrayList<>();
    private final ArrayList<Adapter> ADAPTERS = new ArrayList<>();
    String TAG;
    boolean testMode;
    private boolean showable = false;
    private Listener listener;
    private ClickListener clickListener;
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
    private boolean showAndFinish;
    private long timeCap;
    private long lastTimeShowed;

    AdManager(Activity activity) {
        RemoteConfigHelper.init(activity);
        this.context = activity;
        String tag = activity.getClass().getSimpleName();
        this.TAG = "ADM_" + (globalCounter++) + "_" + tag;
        this.TAG = this.TAG.substring(0, Math.min(23, this.TAG.length()));

        context.getApplication().registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
    }

    Activity getActivity() {
        return this.context;
    }

    void setIntent(Intent intent) {
        this.intent = intent;
    }

    AdManager add(Adapter adapter) {
        adapter.setOrder(ENABLE_KEYS.size());
        adapter.setManager(this);
        ENABLE_KEYS.add(adapter.getEnableKey());
        LOADED.add(false);
        SKIP.add(false);
        ADAPTER_FINISH_LISTENER_CALLED.add(false);
        ADAPTERS.add(adapter);
        return this;
    }

    AdManager build(Listener listener, ClickListener clickListener, boolean testMode) {
        Log.d(TAG, "initializing");
        this.listener = listener;
        this.clickListener = clickListener;
        this.testMode = testMode;

        if (ENABLE_KEYS.size() != LOADED.size() || LOADED.size() != SKIP.size()) {
            throw new IllegalStateException("WRONG INITIALIZED ADS");
        }
        for (int i = 0; i < LOADED.size(); i++) {
            LOADED.set(i, false);
            SKIP.set(i, false);
            ADAPTER_FINISH_LISTENER_CALLED.set(i, false);
        }

        for (Adapter adapter : ADAPTERS) {
            boolean remoteValue = RemoteConfigHelper.getConfigs().getValue(adapter.getEnableKey()).getSource() == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE;
            boolean enabled = RemoteConfigHelper.getConfigs().getBoolean(adapter.getEnableKey());
            if (!this.testMode && remoteValue && !enabled) {
                // do not initialize the adapter if remote enable value is FALSE
                adapter.error("not enabled");
            } else {
                adapter.init();
                adapter.startTimer();
            }

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

    public void waitAndShow(long wait) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AdmUtils.isContextInvalid(getActivity())) {
                    return;
                }
                show();
            }
        }, wait);
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

    synchronized void display() {
        display(false);
    }

    synchronized void display(boolean closedRecently) {
        if (AdmUtils.anyFalse(LOADED)) {
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
            if (SKIP.get(i)) {
                adapterFinished(i, false, false);
                continue;
            }
            String enableKey = ENABLE_KEYS.get(i);
            Adapter adapter = ADAPTERS.get(i);

            boolean enabledNotNull = enableKey != null;
            boolean areAdsEnabled = RemoteConfigHelper.areAdsEnabled();
            boolean remoteConfigEnabled = RemoteConfigHelper.getConfigs().getBoolean(enableKey);
            boolean enabled = enabledNotNull && areAdsEnabled && remoteConfigEnabled;

            if (enabled || testMode) {
                Log.d(TAG, "Displaying " + adapter.getAdapterName());
                adapter.show();
                lastTimeShowed = System.currentTimeMillis();
                this.showable = false; // stop showing new ads
                break;
            } else {
                Log.d(TAG, adapter.getAdapterName() + " not enabled");
                SKIP.set(i, true);

                adapterFinished(i, false, true);

                if (stepByStep) {
                    break;
                }
            }
        }

        if (AdmUtils.anyFalse(SKIP)) {
            return;
        }

        if (listener != null) {
            Log.d(TAG, "finished all");
            listener.finishedAll();
        }

        // reload if error occurred
        reload();

        startNextActivity();
    }

    private void startNextActivity() {
        if (intent != null) {
            Log.d(TAG, "Starting Next Activity");
            getActivity().startActivity(intent);
            getActivity().finish();
            intent = null;
        } else {
            if (showAndFinish) {
                getActivity().finish();
            }
        }
    }

    private void reload() {
        if (stepByStep && AdmUtils.allTrue(SKIP)) {
            Log.d(TAG, "Reloading ads");
            destroy();
            build(listener, clickListener, testMode);
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

    void setLoaded(int order, boolean loaded) {
        this.LOADED.set(order, true);
        if (!loaded) {
            setSkip(order);
        }
        // Adapter listener: INITIALIZED
        if (listener instanceof AdapterListener) {
            Class<? extends Adapter> clz = ADAPTERS.get(order).getClass();
            ((AdapterListener) listener).initialized(order, clz, loaded);
        }

        // Listener: INITIALIZED_ALL
        if (AdmUtils.allTrue(LOADED)) {
            Log.d(TAG, "initialized all");
            if (listener != null) {
                ArrayList<Boolean> loadedList = new ArrayList<>();
                for (Boolean bo : SKIP) {
                    loadedList.add(!bo);
                }
                listener.initializedAll(loadedList);
            }
        }
    }

    private void setSkip(int order) {
        this.SKIP.set(order, true);
    }

    void setClosed(int order) {
        this.setSkip(order);
        if (!this.stepByStep) {
            this.showable = true; // enable to display new ads
        }
        this.lastTimeShowed = System.currentTimeMillis();

        // Adapter listener: FINISHED
        adapterFinished(order, true, true);
    }

    void clicked(int i, String adapterName, String subname) {
        if (clickListener == null) {
            return;
        }
        if (subname == null) {
            subname = "";
        } else {
            subname = "_" + subname;
        }
        Class<? extends Adapter> clz = ADAPTERS.get(i).getClass();
        clickListener.clicked(i, clz, adapterName + subname);
    }

    private void adapterFinished(int i, boolean displayed, boolean showOneBarrier) {
        Boolean called = ADAPTER_FINISH_LISTENER_CALLED.get(i);
        if (called) {
            return;
        }
        ADAPTER_FINISH_LISTENER_CALLED.set(i, true);

        // adapter listener
        if (listener instanceof AdapterListener) {
            Class<? extends Adapter> clz = ADAPTERS.get(i).getClass();
            ((AdapterListener) listener).finished(i, clz, displayed, showOneBarrier);
        }
    }

    public interface Listener {
        void finishedAll();

        void initializedAll(List<Boolean> loaded);
    }

    public interface ClickListener {
        void clicked(int order, Class<? extends Adapter> clz, String adapterName);
    }

    public interface AdapterListener extends Listener {
        void finished(int order, Class<? extends Adapter> clz, boolean displayed, boolean showOneBarrier);

        void initialized(int order, Class<? extends Adapter> clz, boolean loaded);
    }

    public abstract static class AAdapterListener implements AdapterListener {
        @Override
        public void finishedAll() {

        }

        @Override
        public void initializedAll(List<Boolean> loaded) {

        }

        @Override
        public void finished(int order, Class<? extends Adapter> clz, boolean displayed, boolean showOneBarrier) {

        }

        @Override
        public void initialized(int order, Class<? extends Adapter> clz, boolean loaded) {

        }
    }

    public abstract static class AListener implements Listener {
        @Override
        public void finishedAll() {

        }

        @Override
        public void initializedAll(List<Boolean> loaded) {

        }
    }

}