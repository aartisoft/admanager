package com.admanager.recyclerview;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.ads.NativeAdsManager;


public abstract class BindableFaceAdViewHolder extends RecyclerView.ViewHolder {
    public BindableFaceAdViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected abstract void bindTo(NativeAdsManager manager);
}
