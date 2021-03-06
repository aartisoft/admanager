package com.admanager.recyclerview;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.facebook.Consts;
import com.admanager.facebook.R;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;

import java.util.List;


/**
 * Created by Gust on 20.11.2018.
 */
public abstract class BaseAdapterWithFaceNative<T, VH extends BindableViewHolder<T>> extends ABaseAdapter<T, VH, AdmAdapterConfiguration<BaseAdapterWithFaceNative.NativeType>> {
    private static final String TAG = "FaceSearchAdapter";
    public static final String FACEBOOK_NATIVE_TEST_ID = "YOUR_PLACEMENT_ID";
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
            if (activity == null) {
                return;
            }
            if (isTestMode()) {
                nativeAdUnitId = FACEBOOK_NATIVE_TEST_ID;
            }
            manager = new NativeAdsManager(activity, nativeAdUnitId, 5);
            manager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    refreshRowWrappers();
                }

                @Override
                public void onAdError(AdError adError) {
                    Log.e(TAG, "Native Ad Failed to load: " + adError.getErrorMessage());
                    BaseAdapterWithFaceNative.super.show_native = false;
                    refreshRowWrappers();
                }
            });
        }
        manager.loadAds();
    }

    @Override
    protected final void fillDefaultTypeOfConfiguration() {
        if (configuration.getType() == null) {
            configuration = configuration.type(NativeType.NATIVE_BANNER);
        }
    }

    @Override
    protected final AdmAdapterConfiguration<NativeType> createDefaultConfiguration() {
        return new AdmAdapterConfiguration<>();
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
            int customLayout = configuration.getCustomNativeLayout();
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
            BindableFaceAdViewHolder customViewHolder = onCreateCustomNativeViewHolder(view);
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

    protected BindableFaceAdViewHolder onCreateCustomNativeViewHolder(View view) {
        return null;
    }

    private BindableFaceAdViewHolder getViewHolder(View view) {
        switch (configuration.getType()) {
            case CUSTOM:
                return onCreateCustomNativeViewHolder(view);
            case NATIVE_LARGE:
            case NATIVE_BANNER:
            default:
                return new FaceNativeAdViewHolder(view);
        }
    }

    @LayoutRes
    private int getLayoutId() {
        switch (configuration.getType()) {
            case CUSTOM:
                return configuration.getCustomNativeLayout();
            case NATIVE_LARGE:
                return R.layout.item_face_native_ad;
            case NATIVE_BANNER:
            default:
                return R.layout.item_face_native_banner_ad;
        }
    }

    public enum NativeType {
        NATIVE_BANNER, NATIVE_LARGE, CUSTOM
    }
}
