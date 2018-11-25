package com.admanager.core;

import android.app.Activity;
import android.content.Intent;

public class AdManagerBuilder {
    private AdManager adManager;
    private AdManager.Listener listener;

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

    public AdManager build() {
        AdManager manager = adManager.build(listener);
        return manager;
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