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
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BaseAdapterWithAdmobNative<T, VH extends BindableViewHolder<T>> extends ABaseAdapter<T, VH> {
    private static final String TAG = "AdmobBaseAdapter";
    private static final String ADMOB_NATIVE_TEST_ID = "ca-app-pub-3940256099942544/2247696110";
    private AdLoader mAdLoader;
    private CopyOnWriteArrayList<UnifiedNativeAd> mAppInstallAd = new CopyOnWriteArrayList<>();

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
            if (activity == null) {
                return;
            }
            if (isTestMode()) {
                nativeAdUnitId = ADMOB_NATIVE_TEST_ID;
            }
            mAdLoader = new AdLoader.Builder(activity, nativeAdUnitId)
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            mAppInstallAd.add(unifiedNativeAd);
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

        AdRequest.Builder builder = new AdRequest.Builder();
        if (testDevice() != null) {
            builder = builder.addTestDevice(testDevice());
        }
        mAdLoader.loadAds(builder.build(), 3);

    }

    protected String testDevice() {
        return null;
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
            BindableAdmobAdViewHolder customViewHolder = getCustomNativeViewHolder(view);
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
        if (holder instanceof BindableAdmobAdViewHolder) {
            UnifiedNativeAd ad = null;
            try {
                int index = (position / super.DEFAULT_NO_OF_DATA_BETWEEN_ADS) % mAppInstallAd.size();
                ad = mAppInstallAd.get(index);
            } catch (Exception ignored) {

            }
            ((BindableAdmobAdViewHolder) holder).bindTo(ad);
        }
    }

    protected BindableAdmobAdViewHolder getCustomNativeViewHolder(View view) {
        return null;
    }

    @LayoutRes
    protected int getCustomNativeLayout() {
        return 0;
    }

    private BindableAdmobAdViewHolder getViewHolder(View view) {
        switch (getNativeType()) {
            case CUSTOM:
                return getCustomNativeViewHolder(view);
            case NATIVE_LARGE:
            case NATIVE_BANNER:
            default:
                return new AdmobNativeAdViewHolder(view);
        }
    }

    @LayoutRes
    private int getLayoutId() {
        switch (getNativeType()) {
            case CUSTOM:
                return getCustomNativeLayout();
            case NATIVE_LARGE:
                return R.layout.ad_native_unified;
            case NATIVE_BANNER:
            default:
                return R.layout.ad_native_unified_sm;
        }
    }

    @NonNull
    protected NativeType getNativeType() {
        return NativeType.NATIVE_BANNER;
    }


    public enum NativeType {
        NATIVE_BANNER, NATIVE_LARGE, CUSTOM
    }
}
