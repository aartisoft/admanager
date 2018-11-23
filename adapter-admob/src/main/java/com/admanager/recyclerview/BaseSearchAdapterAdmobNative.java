package com.admanager.recyclerview;


import android.app.Activity;
import android.support.annotation.NonNull;
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

public class BaseSearchAdapterAdmobNative<T> extends ABaseSearchAdapter<T> {
    private static final String TAG = "AdmobSearchAdapter";
    private AdLoader mAdLoader;
    private CopyOnWriteArrayList<NativeAppInstallAd> mAppInstallAd = new CopyOnWriteArrayList<>();

    public BaseSearchAdapterAdmobNative(Activity activity, List<T> data) {
        super(activity, data);
    }

    public BaseSearchAdapterAdmobNative(Activity activity, List<T> data, boolean show_native, int gridSize, int density, String nativeAdUnitId) {
        super(activity, data, gridSize, show_native, density);
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
                            BaseSearchAdapterAdmobNative.super.show_native = false;
                            refreshRowWrappers();

                        }
                    }).build();
        }

        mAdLoader.loadAds(new AdRequest.Builder().build(), 3);

    }


    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
}
