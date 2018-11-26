package com.admanager.recyclerview;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Gust on 19.12.2017.
 */
public abstract class BindableViewHolder<T> extends RecyclerView.ViewHolder {
    public BindableViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindTo(Activity activity, T model, int position);
}
