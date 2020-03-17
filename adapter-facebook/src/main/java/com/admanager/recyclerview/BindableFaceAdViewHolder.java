package com.admanager.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.NativeAdsManager;


public abstract class BindableFaceAdViewHolder extends RecyclerView.ViewHolder {
    public BindableFaceAdViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected abstract void bindTo(NativeAdsManager manager);
}
