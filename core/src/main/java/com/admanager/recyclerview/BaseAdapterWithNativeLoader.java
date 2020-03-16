package com.admanager.recyclerview;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.admanager.core.NativeLoader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * USAGE:
 * NativeLoader[] nativeLoaders = new NativeLoader[5];
 * for (int i = 0; i < nativeLoaders.length; i++) {
 * LinearLayout linearLayout = new LinearLayout(this);
 * nativeLoaders[i] = new AdMostNativeLoader(this, linearLayout, RCUtils.NATIVE_ADMOB_ENABLED)
 * .size(AdMostNativeLoader.NativeType.NATIVE_BANNER)
 * .withId(Consts.TEST_APP_ID, Consts.TEST_NATIVE_ZONE);
 * }
 */
public abstract class BaseAdapterWithNativeLoader<T, VH extends BindableViewHolder<T>> extends ABaseAdapter<T, VH, AdmAdapterConfiguration<?>> {
    private CopyOnWriteArrayList<NativeLoader> nativeLoaders = new CopyOnWriteArrayList<>();

    public BaseAdapterWithNativeLoader(Activity activity,
                                       @LayoutRes int layout,
                                       List<T> data,
                                       boolean show_native,
                                       NativeLoader... mAdLoaders) {
        super(activity, layout, data, show_native);
        if (activity == null) {
            return;
        }

        if (mAdLoaders != null && super.show_native) {
            for (NativeLoader mAdLoader : mAdLoaders) {
                mAdLoader.setListener(new NativeLoader.Listener() {
                    @Override
                    public void error(String error) {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void loaded() {
                        notifyDataSetChanged();
                    }
                });
                mAdLoader.load();
                nativeLoaders.add(mAdLoader);
            }
        }

    }

    @Override
    protected final AdmAdapterConfiguration<?> createDefaultConfiguration() {
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

            LinearLayout itemView = new LinearLayout(parent.getContext());
            itemView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            itemView.setOrientation(LinearLayout.VERTICAL);

            holder = new NativeLoaderAdViewHolder(itemView);
        }
        return holder;
    }

    @Override
    protected void fillDefaultTypeOfConfiguration() {

    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof NativeLoaderAdViewHolder) {
            NativeLoader ad = null;
            try {
                int index = (position / super.DEFAULT_NO_OF_DATA_BETWEEN_ADS) % nativeLoaders.size();
                ad = nativeLoaders.get(index);
            } catch (Exception ignored) {

            }
            ((NativeLoaderAdViewHolder) holder).bindTo(ad);
        }
    }

}
