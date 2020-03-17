package com.admanager.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        return new AdmAdapterConfiguration<>()
                .density(0);
    }

    @Override
    protected final void fillDefaultTypeOfConfiguration() {

    }

    @Override
    @NonNull
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        View view;

        if (holder != null) {
            return holder;
        } else {
            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            view = layoutInflater.inflate(super.layout, parent, false);
            return createViewHolder(view);
        }
    }

}