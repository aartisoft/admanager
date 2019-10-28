package com.admanager.facebook;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admanager.config.RemoteConfigHelper;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gust on 20.11.2018.
 */
public class FacebookAdHelper {
    private static final String TAG = "MyFacebookHelper";
    /*
     * BANNER AD LOADER
     * */

    public static void showNsecInters(final long N, final Context context, String remoteConfigEnableKey, final String remoteConfigIdKey) {
        showNsecInters(N, context, remoteConfigEnableKey, remoteConfigIdKey, null);
    }

    public static void showNsecInters(final long N, final Context context, String remoteConfigEnableKey, final String remoteConfigIdKey, final Listener listener) {
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
                String adAudienceId = RemoteConfigHelper.getConfigs().getString(remoteConfigIdKey);
                if (RemoteConfigHelper.isTestMode()) {
                    adAudienceId = FacebookAdapter.FACEBOOK_INTERS_TEST_ID;
                }
                final InterstitialAd ad = new InterstitialAd(context, adAudienceId);
                ad.setAdListener(new AbstractAdListener() {
                    @Override
                    public void onAdLoaded(Ad ad2) {
                        super.onAdLoaded(ad2);
                        if (ad.isAdLoaded()) {
                            ad.show();

                        }
                    }

                    @Override
                    public void onError(Ad ad, AdError error) {
                        if (listener != null) {
                            listener.completed(false);
                        }
                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        if (listener != null) {
                            listener.completed(true);
                        }
                    }
                });
                ad.loadAd();
            }
        }, N);
    }

    public interface Listener {
        void completed(boolean displayed);
    }

    public static void attachNativeToContainer(LinearLayout adView, LinearLayout container) {
        if (container == null || adView == null) {
            return;
        }
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        container.addView(adView);
    }

    public static void populateNativeAd(NativeAd nativeAd, LinearLayout adView) {
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView mediaView = adView.findViewById(R.id.media_view);

        String alert = "You should use View with id '%s' in layout file.";
        if (nativeAdIcon == null) {
            throw new IllegalStateException(String.format(alert, "native_ad_icon"));
        }
        if (nativeAdTitle == null) {
            throw new IllegalStateException(String.format(alert, "native_ad_title"));
        }
        if (nativeAdBody == null) {
            throw new IllegalStateException(String.format(alert, "native_ad_body"));
        }
        if (nativeAdCallToAction == null) {
            throw new IllegalStateException(String.format(alert, "native_ad_call_to_action"));
        }
        if (adChoicesContainer == null) {
            throw new IllegalStateException(String.format(alert, "ad_choices_container"));
        }
        if (sponsoredLabel == null) {
            throw new IllegalStateException(String.format(alert, "native_ad_sponsored_label"));
        }

        if (nativeAd == null) {
            adView.setVisibility(View.GONE);
            return;
        }

        adView.setVisibility(View.VISIBLE);

        nativeAd.unregisterView();

        AdOptionsView adOptionsView = new AdOptionsView(adView.getContext(), nativeAd, null);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);


        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();
        if (mediaView != null) {
            clickableViews.add(mediaView);
        }
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        if (mediaView != null) {
            nativeAd.registerViewForInteraction(adView, mediaView, nativeAdIcon, clickableViews);
        } else {
            nativeAd.registerViewForInteraction(adView, nativeAdIcon, clickableViews);
        }

    }


}
