package com.admanager.core;

import android.app.Activity;
import android.content.Intent;

import com.admanager.config.RemoteConfigHelper;

public class AdManagerBuilder {
    private AdManager adManager;
    private AdManager.Listener listener;
    private AdManager.ClickListener clickListener;

    public AdManagerBuilder(Activity activity) {
        adManager = new AdManager(activity);
    }

    public AdManagerBuilder add(Adapter adapter) {
        adManager.add(adapter);
        return this;
    }

    public AdManagerBuilder listener(AdManager.Listener listener) {
        this.listener = listener;
        return this;
    }

    public AdManagerBuilder clickListener(AdManager.ClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public AdManager build() {
        return build(RemoteConfigHelper.isTestMode());
    }

    public AdManager build(boolean testMode) {
        return adManager.build(listener, this.clickListener, testMode);
    }

    public AdManagerBuilder thenStart(Intent intent) {
        adManager.setIntent(intent);
        return this;
    }

    public AdManagerBuilder thenStart(Class nextActivityClass) {
        Intent intent = new Intent(adManager.getActivity(), nextActivityClass);
        return thenStart(intent);
    }

}