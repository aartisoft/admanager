package com.admanager.config;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gust on 10.04.2017.
 */

public class RemoteConfigHelper implements OnCompleteListener<Void> {
    private static final String TAG = "RemoteConfigHelper";


    private static RemoteConfigHelper instance;
    private final FirebaseRemoteConfig mRemoteConfig;
    private boolean adsEnabled = true;


    private RemoteConfigHelper(Map<String, Object> defaultMap, boolean idDeveloperModeEnabled) {
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mRemoteConfig.activateFetched();
        mRemoteConfig.setDefaults(defaultMap);
        mRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(idDeveloperModeEnabled).build());
        mRemoteConfig.fetch(mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0 : 10800).addOnCompleteListener(this);
    }

    public static boolean areAdsEnabled() {
        return getInstance().adsEnabled;
    }

    public static void setAdsEnabled(boolean adsEnabled) {
        getInstance().adsEnabled = adsEnabled;
    }

    public static RemoteConfigHelper init(Context context) {
        if (instance == null) {
            synchronized (RemoteConfigHelper.class) {
                if (instance == null) {
                    if (context != null && context instanceof Activity && ((Activity)context).getApplication() instanceof RemoteConfigApp) {
                        Map<String, Object> remoteConfigDefaults = ((RemoteConfigApp) ((Activity)context).getApplication()).getDefaults();
                        return init(remoteConfigDefaults, BuildConfig.DEBUG);
                    } else {
                        Log.e(TAG, "Initialized with empty default values! Make your application 'implements RemoteConfigApp' and call init(Context)");
                        return init(new HashMap<String, Object>(), false);
                    }
                }
            }
        }
        return instance;
    }

    private static RemoteConfigHelper init(Map<String, Object> defaultMap, boolean idDeveloperModeEnabled) {
        if (instance == null) {
            synchronized (RemoteConfigHelper.class) {
                if (instance == null) {
                    instance = new RemoteConfigHelper(defaultMap, idDeveloperModeEnabled);
                }
            }
        }
        return instance;
    }

    private static RemoteConfigHelper getInstance() {
        if (instance == null) {
            Log.e(TAG, "Not initialized yet! Call init() method first, or make your application 'RemoteConfigApp'");
            init((Context) null);
        }
        return instance;
    }


    public static FirebaseRemoteConfig getConfigs() {
        return getInstance().mRemoteConfig;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            mRemoteConfig.activateFetched();
            Log.i(TAG, "remote configs fetched");
        }
    }
}
