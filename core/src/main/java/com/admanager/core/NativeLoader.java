package com.admanager.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;

public abstract class NativeLoader<L extends NativeLoader> {
    public static final String TAG = "NativeLoader";
    private final LinearLayout adContainer;
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
    private Listener listener;
    private String enableKey;

    public NativeLoader(Activity activity, LinearLayout adContainer, String enableKey) {
        RemoteConfigHelper.init(activity);
        adContainer.setOrientation(LinearLayout.VERTICAL);
        this.activity = activity;
        this.enableKey = enableKey;
        this.adContainer = adContainer;

        if (!isEnabled()) {
            hideLayout();
        }

        this.activity.getApplication().registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
    }

    public Listener getListener() {
        return listener;
    }

    public L setListener(Listener listener) {
        this.listener = listener;
        return (L) this;
    }

    private boolean isActivityEquals(Activity activity) {
        return activity.getClass().getName().equals(getActivity().getClass().getName());
    }

    protected final boolean isEnabled() {
        return (RemoteConfigHelper.areAdsEnabled() && RemoteConfigHelper.getConfigs().getBoolean(enableKey)) || isTestMode();
    }

    protected void error(String error) {
        Log.e(TAG, getClass().getSimpleName() + ": " + error);
        hideLayout();
        if (listener != null) {
            listener.error(error);
        }
    }

    private void hideLayout() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            adContainer.setVisibility(View.GONE);
        } else {
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adContainer.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    protected Activity getActivity() {
        return activity;
    }

    public void destroy() {

    }

    protected void loaded(View view) {
        adContainer.setVisibility(View.VISIBLE);
        adContainer.removeAllViews();

        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            adContainer.addView(view);

            if (listener != null) {
                listener.loaded();
            }
        }

    }

    protected LinearLayout getAdContainer() {
        return adContainer;
    }

    protected boolean isTestMode() {
        return RemoteConfigHelper.isTestMode();
    }

    public interface Listener {
        void error(String error);

        void loaded();
    }
}
