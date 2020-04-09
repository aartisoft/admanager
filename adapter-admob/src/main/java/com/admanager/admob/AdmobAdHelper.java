package com.admanager.admob;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.admanager.config.RemoteConfigHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class AdmobAdHelper {

    private static final String TAG = "MyAdmobHelper";

    public static void showNsecInters(final long N, final Context context, String remoteConfigEnableKey, final String remoteConfigIdKey) {
        showNsecInters(N, context, remoteConfigEnableKey, remoteConfigIdKey, null);
    }

    public static void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (adView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (adView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                if (nativeAd.getImages() == null || nativeAd.getImages().size() == 0 || nativeAd.getImages().get(0) == null) {
                    adView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) adView.getIconView()).setImageDrawable(
                            nativeAd.getImages().get(0).getDrawable());
                    adView.getIconView().setVisibility(View.VISIBLE);
                }
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getPriceView() != null) {
            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        }

        if (adView.getStoreView() != null) {
            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        }

        if (adView.getStarRatingView() != null) {
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getAdvertiserView() != null) {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }
        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    public static void showNsecInters(final long N, final Context context, String remoteConfigEnableKey, final String remoteConfigIdKey, final Listener listener) {
        showNsecInters(N, context, remoteConfigEnableKey, remoteConfigIdKey, listener, null);
    }

    public static void showNsecInters(final long N, final Context context, String remoteConfigEnableKey, final String remoteConfigIdKey, final Listener listener, final ClickListener clicklistener) {
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
                String adUnitId = RemoteConfigHelper.getConfigs().getString(remoteConfigIdKey);
                if (RemoteConfigHelper.isTestMode()) {
                    adUnitId = AdmobAdapter.ADMOB_INTERS_TEST_ID;
                }
                final InterstitialAd ad = new InterstitialAd(context);
                ad.setAdUnitId(adUnitId);
                ad.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        if (ad.isLoaded()) {
                            ad.show();
                        }
                    }

                    @Override
                    public void onAdClosed() {
                        if (listener != null) {
                            listener.completed(true);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        if (listener != null) {
                            listener.completed(false);
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        if (clicklistener != null) {
                            clicklistener.clicked();
                        }
                    }
                });
                ad.loadAd(new AdRequest.Builder().build());
            }
        }, N);
    }

    public interface Listener {
        void completed(boolean displayed);
    }

    public interface ClickListener {
        void clicked();
    }
}
