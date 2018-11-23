package com.admanager.admob;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.admanager.config.RemoteConfigHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;

import java.util.List;

public class AdmobAdHelper {

    private static final String TAG = "MyAdmobHelper";

    public static void loadBanner(final Activity context, final LinearLayout adContainer, String remoteConfigBannerEnabledKey, String remoteConfigBannerAdKey) {
        if (!RemoteConfigHelper.getConfigs(context).getBoolean(remoteConfigBannerEnabledKey) || !RemoteConfigHelper.areAdsEnabled(context)) {
            adContainer.setVisibility(View.GONE);
            return;
        }
        adContainer.setVisibility(View.VISIBLE);
        adContainer.removeAllViews();

        String adUnitId = RemoteConfigHelper.getConfigs(context).getString(remoteConfigBannerAdKey);

        AdView mAdView = new AdView(context);
        mAdView.setAdUnitId(adUnitId);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        MobileAds.initialize(context, mAdView.getAdUnitId());

        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest adRequest = builder.build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                adContainer.setVisibility(View.GONE);
                Log.e(TAG, "onAdFailedToLoad: " + i);
            }

            @Override
            public void onAdLoaded() {

            }
        });
        mAdView.loadAd(adRequest);
        adContainer.addView(mAdView);
    }


    public static void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd, NativeAppInstallAdView adView) {
        VideoController videoController = nativeAppInstallAd.getVideoController();

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                .getDrawable());

        MediaView sampleMediaView = adView.findViewById(R.id.appinstall_media);
        ImageView imageView = adView.findViewById(R.id.appinstall_image);

        if (videoController.hasVideoContent()) {
            imageView.setVisibility(View.GONE);
            adView.setMediaView(sampleMediaView);
        } else {
            sampleMediaView.setVisibility(View.GONE);
            adView.setImageView(imageView);

            List<NativeAd.Image> images = nativeAppInstallAd.getImages();

            if (images.size() > 0) {
                ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
            }
        }

        // Some aren't guaranteed, however, and should be checked.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

    }
}
