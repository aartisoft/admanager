package com.admanager.admost;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.admanager.config.RemoteConfigHelper;

import admost.sdk.AdMostInterstitial;
import admost.sdk.listener.AdMostAdListener;

public class AdMostAdHelper {

    private static final String TAG = "AdMostAdHelper";


    public static void showNsecInters(final long N, final Activity context, final String remoteConfigEnableKey,
                                      final String appIdKey, final String zoneIdKey, final Listener listener) {
        showNsecInters(N, context, remoteConfigEnableKey, appIdKey, zoneIdKey, null, listener);
    }

    public static void showNsecInters(final long N, final Activity context, final String remoteConfigEnableKey,
                                      final String appIdKey, final String zoneIdKey, final String tag, final Listener listener) {
        RemoteConfigHelper.init(context);
        boolean enable = RemoteConfigHelper.getConfigs().getBoolean(remoteConfigEnableKey) && RemoteConfigHelper.areAdsEnabled();
        if (RemoteConfigHelper.isTestMode()) {
            enable = true;
        }
        if (!enable) {
            if (listener != null) {
                listener.completed(false);
            }
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String zoneId = RemoteConfigHelper.getConfigs().getString(zoneIdKey);
                String appId = RemoteConfigHelper.getConfigs().getString(appIdKey);
                if (RemoteConfigHelper.isTestMode()) {
                    zoneId = Consts.TEST_FULLSCREEN_ZONE;
                    appId = Consts.TEST_APP_ID;
                }

                com.admanager.admost.Consts.initAdMost(context, appId);
                final AdMostInterstitial ad = new AdMostInterstitial(context, zoneId, null);
                ad.setListener(new AdMostAdListener() {
                    @Override
                    public void onDismiss(String message) {
                        if (listener != null) {
                            listener.completed(true);
                        }
                        // It indicates that the interstitial ad is closed by clicking cross button/back button
                    }

                    @Override
                    public void onFail(int errorCode) {
                        Log.e(TAG, "code:" + errorCode + " " + AdMostAdapter.logError(errorCode));
                        if (listener != null) {
                            listener.completed(false);
                        }

                    }

                    @Override
                    public void onReady(String network, int ecpm) {
                        if (ad.isLoaded()) {
                            if (tag == null) {
                                ad.show();
                            } else {
                                ad.show(tag);
                            }
                        }
                    }

                    @Override
                    public void onShown(String network) {
                        // It indicates that the loaded interstitial ad is shown to the user.
                    }

                    @Override
                    public void onClicked(String s) {
                        // It indicates that the interstitial ad is clicked.
                    }

                    @Override
                    public void onComplete(String s) {
                        // If you are using interstitial, this callback will not be triggered.
                    }
                });
                ad.refreshAd(false);

            }
        }, N);
    }

    public interface Listener {
        void completed(boolean displayed);
    }
}
