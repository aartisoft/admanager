package com.admanager.recyclerview;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Gust on 20.11.2018.
 */
public abstract class BindableViewHolder<T> extends RecyclerView.ViewHolder {
    public BindableViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindTo(Activity activity, T model, int position);
}
