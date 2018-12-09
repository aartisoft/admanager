package com.admanager.recyclerview;


import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admanager.admob.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BaseAdapterWithAdmobNative<T, VH extends BindableViewHolder<T>> extends ABaseAdapter<T, VH> {
    private static final String TAG = "AdmobSearchAdapter";
    private AdLoader mAdLoader;
    private CopyOnWriteArrayList<NativeAppInstallAd> mAppInstallAd = new CopyOnWriteArrayList<>();

    public BaseAdapterWithAdmobNative(Activity activity, @LayoutRes int layout, List<T> data) {
        super(activity, layout, data);
    }

    public BaseAdapterWithAdmobNative(Activity activity,
                                      @LayoutRes int layout,
                                      List<T> data,
                                      boolean show_native,
                                      @Size(min = com.admanager.admob.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.admob.Consts.AD_UNIT_SIZE_MAX) String nativeAdUnitId) {
        super(activity, layout, data, show_native);
        if (mAdLoader == null) {
            mAdLoader = new AdLoader.Builder(activity, nativeAdUnitId)
                    .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        @Override
                        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                            mAppInstallAd.add(nativeAppInstallAd);
                            refreshRowWrappers();
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            Log.e("NativeAdHelper", "App Install Ad Failed to load: " + errorCode);
                            BaseAdapterWithAdmobNative.super.show_native = false;
                            refreshRowWrappers();

                        }
                    }).build();
        }

        mAdLoader.loadAds(new AdRequest.Builder().build(), 3);

    }


    @Override
    @NonNull
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        View view;

        if (holder != null) {
            return holder;
        } else if (viewType == RowWrapper.Type.NATIVE_AD.ordinal()) {
            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            view = layoutInflater.inflate(R.layout.ad_app_install, parent, false);
            holder = new AdmobNativeAdViewHolder(view);
        }
        return holder;
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof AdmobNativeAdViewHolder) {
            NativeAppInstallAd ad = null;
            try {
                int index = (position / super.DEFAULT_NO_OF_DATA_BETWEEN_ADS) % mAppInstallAd.size();
                ad = mAppInstallAd.get(index);
            } catch (Exception ignored) {

            }
            ((AdmobNativeAdViewHolder) holder).bindTo(ad);
        }
    }

    /** todo
     * public enum NativeType {
     *       NATIVE_BANNER, NATIVE_LARGE,
     *       NATIVE_BANNER_CONTENT_AD, NATIVE_LARGE_CONTENT_AD,
     *       CUSTOM
     * }
     * */
}
