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

import com.admanager.facebook.Consts;
import com.admanager.facebook.R;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;

import java.util.List;


/**
 * Created by Gust on 20.11.2018.
 */
public abstract class BaseAdapterWithFaceNative<T, VH extends BindableViewHolder<T>> extends ABaseAdapter<T, VH> {
    private static final String TAG = "FaceSearchAdapter";
    private NativeAdsManager manager;

    public BaseAdapterWithFaceNative(Activity activity, @LayoutRes int layout) {
        super(activity,  layout);
    }

    public BaseAdapterWithFaceNative(Activity activity, @LayoutRes int layout, List<T> data) {
        super(activity, layout, data);
    }

    public BaseAdapterWithFaceNative(Activity activity, @LayoutRes int layout, List<T> data, boolean show_native, @Size(min = Consts.AD_UNIT_SIZE_MIN, max = Consts.AD_UNIT_SIZE_MAX) String nativeAdUnitId) {
        super(activity, layout, data, show_native);
        if (manager == null) {
            manager = new NativeAdsManager(activity, nativeAdUnitId, 5);
            manager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    refreshRowWrappers();
                }

                @Override
                public void onAdError(AdError adError) {
                    Log.e(TAG, "App Install Ad Failed to load: " + adError.getErrorMessage());
                    BaseAdapterWithFaceNative.super.show_native = false;
                    refreshRowWrappers();
                }
            });
        }
        manager.loadAds();
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

            //////////////////
            ///// Layout /////
            //////////////////
            int layout = getLayoutId();
            if (layout == 0) {
                throw new IllegalArgumentException("Override 'getCustomLayout()' and give valid Layout ID");
            }

            // use Custom Layout if user filled
            int customLayout = getCustomNativeLayout();
            if (customLayout != 0) {
                layout = customLayout;
            }

            view = layoutInflater.inflate(layout, parent, false);

            ///////////////////
            /// View Holder ///
            ///////////////////
            RecyclerView.ViewHolder vh = getViewHolder(view);
            if (vh == null) {
                throw new IllegalArgumentException("Override 'getCustomViewHolder()' and give valid ViewHolder");
            }

            // use Custom View Holder  if user filled
            BindableFaceAdViewHolder customViewHolder = getCustomNativeViewHolder(view);
            if (customViewHolder != null) {
                vh = customViewHolder;
            }

            holder = vh;
        }
        return holder;
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof BindableFaceAdViewHolder) {
            ((BindableFaceAdViewHolder) holder).bindTo(manager);
        }
    }

    public BindableFaceAdViewHolder getCustomNativeViewHolder(View view) {
        return null;
    }

    @LayoutRes
    public int getCustomNativeLayout() {
        return 0;
    }

    private BindableFaceAdViewHolder getViewHolder(View view) {
        switch (getNativeType()) {
            case CUSTOM:
                return getCustomNativeViewHolder(view);
            case NATIVE_LARGE:
                return new FaceNativeAdViewHolder(view);
            case NATIVE_BANNER:
            default:
                return new FaceNativeBannerAdViewHolder(view);
        }
    }

    @LayoutRes
    private int getLayoutId() {
        switch (getNativeType()) {
            case CUSTOM:
                return getCustomNativeLayout();
            case NATIVE_LARGE:
                return R.layout.item_face_native_ad;
            case NATIVE_BANNER:
            default:
                return R.layout.item_face_native_banner_ad;
        }
    }

    @NonNull
    public NativeType getNativeType() {
        return NativeType.NATIVE_BANNER;
    }


    public enum NativeType {
        NATIVE_BANNER, NATIVE_LARGE, CUSTOM
    }
}
