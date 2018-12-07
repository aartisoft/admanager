package com.admanager.unity;


import android.app.Activity;
import android.util.Log;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;

import java.util.HashMap;
import java.util.Map;

class UnityRouter {
    private static final String TAG = "UnityRouter";
    private static final UnityAdsListener sUnityAdsListener = new UnityAdsListener();
    private static String sCurrentPlacementId;
    private static boolean initialized;
    private static Map<String, IUnityAdsExtendedListener> mUnityAdsListeners = new HashMap<>();

    static boolean isInitialized() {
        return initialized;
    }

    static boolean initUnityAds(String gameId, Activity launcherActivity, boolean testMode) {
        if (gameId == null || gameId.isEmpty()) {
            return false;
        }

        UnityAds.initialize(launcherActivity, gameId, sUnityAdsListener, testMode);
        initialized = true;
        return true;
    }


    static void showAd(Activity activity, String placementId) {
        sCurrentPlacementId = placementId;
        UnityAds.show(activity, placementId);
    }

    static void addListener(String placementId, IUnityAdsExtendedListener unityListener) {
        mUnityAdsListeners.put(placementId, unityListener);
    }

    static void removeListener(String placementId) {
        mUnityAdsListeners.remove(placementId);
    }

    private static class UnityAdsListener implements IUnityAdsExtendedListener {
        @Override
        public void onUnityAdsReady(String placementId) {
            Log.v(TAG, "ready: " + placementId);
            IUnityAdsExtendedListener listener = mUnityAdsListeners.get(placementId);
            if (listener != null) {
                listener.onUnityAdsReady(placementId);
            }
        }

        @Override
        public void onUnityAdsStart(String placementId) {
            Log.v(TAG, "onUnityAdsStart: " + placementId);
            IUnityAdsExtendedListener listener = mUnityAdsListeners.get(placementId);
            if (listener != null) {
                listener.onUnityAdsStart(placementId);
            }
        }

        @Override
        public void onUnityAdsFinish(String placementId, UnityAds.FinishState finishState) {
            Log.v(TAG, "onUnityAdsFinish: " + placementId);
            IUnityAdsExtendedListener listener = mUnityAdsListeners.get(placementId);
            if (listener != null) {
                listener.onUnityAdsFinish(placementId, finishState);
            }
        }

        @Override
        public void onUnityAdsClick(String placementId) {
            IUnityAdsExtendedListener listener = mUnityAdsListeners.get(placementId);
            if (listener != null) {
                listener.onUnityAdsClick(placementId);
            }
        }

        // @Override
        public void onUnityAdsPlacementStateChanged(String placementId, UnityAds.PlacementState oldState, UnityAds.PlacementState newState) {
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String message) {
            Log.v(TAG, "onUnityAdsError: " + sCurrentPlacementId + " " + unityAdsError + ":" + message);
            IUnityAdsExtendedListener listener = mUnityAdsListeners.get(sCurrentPlacementId);
            if (listener != null) {
                listener.onUnityAdsError(unityAdsError, message);
            }
        }
    }
}