package com.admanager.recyclerview;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admanager.facebook.R;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;

import java.util.List;


/**
 * Created by Gust on 19.12.2017.
 */
public abstract class BaseSearchAdapterFaceNative<T> extends ABaseSearchAdapter<T> {
    private static final String TAG = "FaceSearchAdapter";
    private NativeAdsManager manager;

    public BaseSearchAdapterFaceNative(Activity activity, List<T> data) {
        super(activity, data);
    }

    public BaseSearchAdapterFaceNative(Activity activity, List<T> data, boolean show_native, int gridSize, int density, String nativeAdUnitId) {
        super(activity, data, gridSize, show_native, density);
        if (manager == null) {
            manager = new NativeAdsManager(activity, nativeAdUnitId, 5);
            manager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    refreshRowWrappers();
                    activity.runOnUiThread(() -> notifyDataSetChanged());
                }

                @Override
                public void onAdError(AdError adError) {
                    Log.e(TAG, "App Install Ad Failed to load: " + adError.getErrorMessage());
                    BaseSearchAdapterFaceNative.super.show_native = false;
                    refreshRowWrappers();
                    activity.runOnUiThread(() -> notifyDataSetChanged());
                }
            });
        }
        manager.loadAds();
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
            view = layoutInflater.inflate(R.layout.face_native_ad_mini, parent, false);
            holder = new FaceNativeMiniAdViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof FaceNativeMiniAdViewHolder) {
            ((FaceNativeMiniAdViewHolder) holder).bindTo(manager);
        }
    }
}
