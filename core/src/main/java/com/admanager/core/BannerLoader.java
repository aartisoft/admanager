package com.admanager.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.admanager.config.RemoteConfigHelper;

public abstract class BannerLoader<L extends BannerLoader> {
    public static final int DEFAULT_BORDER_SIZE = 2;
    public static final int DEFAULT_BORDER_COLOR = R.color.colorPrimary;
    private final String adapterName;
    private final LinearLayout container;
    private final LinearLayout adContainer;
    public String TAG;
    private ClickListener clickListener;
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
            onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            onPause();
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
    private Integer topSizeDp;
    private Integer topColor;
    private Integer bottomSizeDp;
    private Integer bottomColor;
    private String enableKey;

    public BannerLoader(Activity activity, String adapterName, LinearLayout adContainer, String enableKey) {
        RemoteConfigHelper.init(activity);
        adContainer.setOrientation(LinearLayout.VERTICAL);
        this.activity = activity;
        this.adapterName = adapterName;
        this.enableKey = enableKey;
        this.container = adContainer;
        this.adContainer = new LinearLayout(getActivity());
        this.adContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.adContainer.setOrientation(LinearLayout.VERTICAL);

        this.TAG = "ADM_BannerLoader";
        this.TAG = this.TAG.substring(0, Math.min(23, this.TAG.length()));

        if (!isEnabled()) {
            Log.i(TAG, getAdapterName() + ": not enabled");
            hideLayout();
        }

        this.activity.getApplication().registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
    }

    private boolean isActivityEquals(Activity activity) {
        return activity.getClass().getName().equals(getActivity().getClass().getName());
    }

    protected final boolean isEnabled() {
        boolean adsEnabled = RemoteConfigHelper.areAdsEnabled() && RemoteConfigHelper.getConfigs().getBoolean(enableKey);
        boolean testMode = isTestMode();
        if (!(adsEnabled || testMode)) {
            Log.d(TAG, getAdapterName() + ": not enabled");
        }
        return adsEnabled || testMode;
    }

    protected void error(String error) {
        Log.e(TAG, getAdapterName() + ": " + error);
        hideLayout();
    }

    protected void clicked() {
        clicked(null, null);
    }

    protected void clicked(final String subname, final Double revenue) {
        Log.v(TAG, getAdapterName() + ": clicked");
        if (clickListener == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String name = getAdapterName();
                if (subname != null) {
                    name = name + "_" + subname;
                }
                clickListener.clicked(name, revenue);
            }
        });
    }

    protected void logv(String s) {
        Log.v(TAG, getAdapterName() + ": " + s);
    }

    protected void loge(String s) {
        Log.e(TAG, getAdapterName() + ": " + s);
    }

    private String getAdapterName() {
        return adapterName;
    }

    private void hideLayout() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            container.setVisibility(View.GONE);
        } else {
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        container.setVisibility(View.GONE);
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

    public void onResume() {
    }

    public void onPause() {
    }

    protected void initContainer() {
        initContainer(null);
    }

    protected void initContainer(View view) {
        container.setVisibility(View.VISIBLE);
        container.removeAllViews();

        addBorderView(topSizeDp, topColor);
        container.addView(adContainer);
        addBorderView(bottomSizeDp, bottomColor);

        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            adContainer.addView(view);

            Log.d(TAG, getAdapterName() + ": loaded");
        }
    }

    private void addBorderView(Integer sizeDp, Integer color) {
        if (color == null || sizeDp == null) {
            return;
        }
        LinearLayout border = new LinearLayout(getActivity());
        int borderSize = (int) AdmUtils.dpToPx(getActivity(), sizeDp);
        border.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, borderSize));
        border.setOrientation(LinearLayout.VERTICAL);
        border.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        container.addView(border);
    }

    protected LinearLayout getAdContainer() {
        return adContainer;
    }

    public L withClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
        return (L) this;
    }

    public L withBorderSize(Integer sizeDp) {
        this.topSizeDp = sizeDp;
        this.bottomSizeDp = sizeDp;

        if (this.topColor == null) {
            this.topColor = DEFAULT_BORDER_COLOR;
        }

        if (this.bottomColor == null) {
            this.bottomColor = DEFAULT_BORDER_COLOR;
        }
        return (L) this;
    }

    public L withBorderColor(@ColorRes Integer colorRes) {
        this.bottomColor = colorRes;
        this.topColor = colorRes;

        if (this.topSizeDp == null) {
            this.topSizeDp = DEFAULT_BORDER_SIZE;
        }

        if (this.bottomSizeDp == null) {
            this.bottomSizeDp = DEFAULT_BORDER_SIZE;
        }

        return (L) this;
    }

    public L withBorderTop(Integer topSizeDp, @ColorRes Integer topColor) {
        this.topSizeDp = topSizeDp;
        this.topColor = topColor;
        return (L) this;
    }

    public L withBorderBottom(Integer bottomSizeDp, @ColorRes Integer bottomColor) {
        this.bottomSizeDp = bottomSizeDp;
        this.bottomColor = bottomColor;
        return (L) this;
    }

    public L withBorder(Integer sizeDp, @ColorRes Integer colorRes) {
        withBorderBottom(sizeDp, colorRes);
        return withBorderTop(sizeDp, colorRes);
    }

    public L withBorder() {
        withBorderTop(DEFAULT_BORDER_SIZE, DEFAULT_BORDER_COLOR);
        return withBorderBottom(DEFAULT_BORDER_SIZE, DEFAULT_BORDER_COLOR);
    }

    public boolean hasBorder() {
        return topColor != null || bottomColor != null;
    }

    public L withBorderTop() {
        return withBorderTop(DEFAULT_BORDER_SIZE, DEFAULT_BORDER_COLOR);
    }

    public L withBorderBottom() {
        return withBorderBottom(DEFAULT_BORDER_SIZE, DEFAULT_BORDER_COLOR);
    }

    protected boolean isTestMode() {
        return RemoteConfigHelper.isTestMode();
    }

    public interface ClickListener {
        void clicked(String adapterName, Double revenue);
    }
}
