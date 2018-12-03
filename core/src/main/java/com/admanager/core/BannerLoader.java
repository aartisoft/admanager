package com.admanager.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.admanager.config.RemoteConfigHelper;

public abstract class BannerLoader {
    public static final String TAG = "AdManagerBanner";
    private Activity activity;
    private final Application.ActivityLifecycleCallbacks LIFECYCLE_CALLBACKS = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }


        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

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
                activity.getApplication().unregisterActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
            }
        }
    };
    private String enableKey;

    public BannerLoader(Activity activity, String enableKey) {
        RemoteConfigHelper.init(activity);
        this.activity = activity;
        this.enableKey = enableKey;

        this.activity.getApplication().registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
    }

    private boolean isActivityEquals(Activity activity) {
        return activity.getClass().getName().equals(getActivity().getClass().getName());
    }

    protected final boolean isEnabled() {
        return RemoteConfigHelper.areAdsEnabled() && RemoteConfigHelper.getConfigs().getBoolean(enableKey);
    }

    public void error(String error) {
        Log.e(TAG, error);
    }

    public Activity getActivity() {
        return activity;
    }

    public void destroy() {

    }
}
