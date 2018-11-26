package com.admanager.recyclerview;


import android.app.Activity;
import android.support.annotation.LayoutRes;

import java.util.List;


public abstract class BaseAdapter<T, VH extends BindableViewHolder<T>> extends ABaseAdapter<T, VH> {
    private static final String TAG = "BaseAdapter";

    public BaseAdapter(Activity activity, Class<VH> vhClass, int layout) {
        super(activity, vhClass, layout);
    }

    public BaseAdapter(Activity activity, Class<VH> vhClass, @LayoutRes int layout, List<T> data) {
        super(activity, vhClass, layout, data);
    }

}