package com.admanager.facebook;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admanager.config.RemoteConfigHelper;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeBannerAd;

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
        RemoteConfigHelper.init(context);
        boolean enable = RemoteConfigHelper.getConfigs().getBoolean(remoteConfigEnableKey) && RemoteConfigHelper.areAdsEnabled();
        if (RemoteConfigHelper.isTestMode()) {
            enable = true;
        }
        if (!enable) {
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
                });
                ad.loadAd();
            }
        }, N);
    }


    private static void inflateMiniNativeAd(Activity context, NativeBannerAd nativeAd, LinearLayout adContainer) {
        nativeAd.unregisterView();

        NativeAdLayout nativeAdLayout = new NativeAdLayout(context);
        adContainer.removeAllViews();
        adContainer.addView(nativeAdLayout);

        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        View adView = inflater.inflate(R.layout.item_face_native_banner_ad, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);


        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdIcon,
                clickableViews);
    }


}
