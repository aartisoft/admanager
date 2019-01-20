package com.admanager.ironsource;

import android.support.annotation.Size;
import android.text.TextUtils;

import com.admanager.core.Adapter;
import com.admanager.core.Consts;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;


public class IronsourceAdapter extends Adapter {
    private String appKey;
    private String placement;

    private InterstitialListener LISTENER = new InterstitialListener() {
        @Override
        public void onInterstitialAdReady() {
            logv("onInterstitialAdReady");
            loaded();
        }

        @Override
        public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
            logv("onInterstitialAdLoadFailed");
            IronSource.removeInterstitialListener();
            error(ironSourceError != null ? (ironSourceError.getErrorCode() + ":" + ironSourceError.getErrorMessage()) : "");
        }

        @Override
        public void onInterstitialAdOpened() {
            logv("onInterstitialAdOpened");

        }

        @Override
        public void onInterstitialAdClosed() {
            logv("onInterstitialAdClosed");
            IronSource.removeInterstitialListener();
            closed();
        }

        @Override
        public void onInterstitialAdShowSucceeded() {
            logv("onInterstitialAdShowSucceeded");

        }

        @Override
        public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
            logv("onInterstitialAdShowFailed");
            IronSource.removeInterstitialListener();
            error(ironSourceError != null ? (ironSourceError.getErrorCode() + ":" + ironSourceError.getErrorMessage()) : "");
        }

        @Override
        public void onInterstitialAdClicked() {
            logv("onInterstitialAdClicked");

        }
    };


    public IronsourceAdapter(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(rcEnableKey);
    }

    @Override
    protected String getAdapterName() {
        return "Ironsource";
    }

    public IronsourceAdapter withId(@Size(min = 5, max = 10) String appKey, @Size(min = 2, max = 20) String placement) {
        if (this.appKey != null) {
            throw new IllegalStateException("You already set appKey with 'withRemoteConfig' method");
        }
        this.appKey = appKey;
        this.placement = placement;
        return this;
    }

    @Override
    protected void init() {
        if (TextUtils.isEmpty(this.appKey)) {
            throw new IllegalStateException("call 'withId' or 'withRemoteConfigId' method after adapter creation.");
        }

        IronSource.init(getActivity(), appKey, IronSource.AD_UNIT.INTERSTITIAL);

        IronSource.setInterstitialListener(LISTENER);
        IronSource.loadInterstitial();
    }

    @Override
    protected void destroy() {

    }

    @Override
    protected void show() {
        if (IronSource.isInterstitialReady()) {
            if (this.placement == null) {
                IronSource.showInterstitial();
            } else {
                IronSource.showInterstitial(this.placement);
            }
        } else {
            closed();
            loge("NOT LOADED");
        }
    }

}