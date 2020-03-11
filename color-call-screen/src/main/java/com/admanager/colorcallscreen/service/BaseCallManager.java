package com.admanager.colorcallscreen.service;

import android.content.Context;

import java.lang.ref.WeakReference;

abstract class BaseCallManager implements ICallManagerCompat {
    private static final String TAG = "CallManagerCompat";
    final WeakReference<Context> context;

    BaseCallManager(Context context) {
        this.context = new WeakReference<>(context.getApplicationContext());
    }

}
