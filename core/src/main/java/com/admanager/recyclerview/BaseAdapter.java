package com.admanager.recyclerview;


import android.app.Activity;
import android.support.annotation.LayoutRes;

import java.util.List;

public abstract class BaseAdapter<T, VH extends BindableViewHolder<T>> extends ABaseAdapter<T, VH, AdmAdapterConfiguration<?>> {
    private static final String TAG = "BaseAdapter";

    public BaseAdapter(Activity activity, int layout) {
        super(activity, layout);
    }

    public BaseAdapter(Activity activity, @LayoutRes int layout, List<T> data) {
        super(activity, layout, data);
    }

    @Override
    protected final AdmAdapterConfiguration<?> createDefaultConfiguration() {
        return new AdmAdapterConfiguration<>();
    }
}