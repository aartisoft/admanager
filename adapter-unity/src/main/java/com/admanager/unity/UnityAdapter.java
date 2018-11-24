package com.admanager.unity;

import android.util.Log;

import com.admanager.core.Adapter;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;


public class UnityAdapter extends Adapter implements IUnityAdsExtendedListener {


    private final String gameId;
    private final String placementId;

    public UnityAdapter(String enableKey, String gameId, String placementId) {
        super(enableKey);
        this.gameId = gameId;
        this.placementId = placementId;

    }

    @Override
    protected void init() {
        UnityRouter.addListener(placementId, this);

        if (!UnityAds.isInitialized()) {
            UnityRouter.initUnityAds(gameId, placementId, getActivity());
        }
    }

    @Override
    protected void destroy() {
        UnityRouter.removeListener(placementId);
    }

    @Override
    protected void show() {
        if (UnityAds.isReady(placementId)) {
            UnityRouter.showAd(getActivity(), placementId);
        } else {
            closed();
            Log.e("AdManager2", "NOT LOADED");
        }
    }


    @Override
    public void onUnityAdsReady(String placementId) {
        loaded();
    }

    @Override
    public void onUnityAdsStart(String placementId) {

    }

    @Override
    public void onUnityAdsFinish(String placementId, UnityAds.FinishState finishState) {
        if (finishState == UnityAds.FinishState.ERROR) {
            error("playback: " + placementId);
        } else {
            closed();
        }
        UnityRouter.removeListener(placementId);
    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String message) {
        error("code:" + unityAdsError + " / " + message);
        UnityRouter.removeListener(placementId);
    }


    @Override
    public void onUnityAdsClick(String placementId) {
    }

    @Override
    public void onUnityAdsPlacementStateChanged(String placementId, UnityAds.PlacementState placementState, UnityAds.PlacementState placementState1) {

    }
}