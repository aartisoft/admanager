package com.admanager.recyclerview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.admanager.core.NativeLoader;

public class NativeLoaderAdViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "NativeLoaderAdViewHoldr";

    private LinearLayout root;

    public NativeLoaderAdViewHolder(View itemView) {
        super(itemView);
        root = (LinearLayout) this.itemView;
    }

    public void bindTo(NativeLoader nativeLoader) {
        if (nativeLoader == null) {
            root.setVisibility(View.GONE);
            return;
        }

        root.setVisibility(View.VISIBLE);
        LinearLayout adContainer = nativeLoader.getAdContainer();
        ViewGroup parent = (ViewGroup) adContainer.getParent();
        if (parent == null) {
            // not loaded yet
            return;
        }
        if (parent.getParent() == null) {
            // not attached to recycler view
            if (root.getChildAt(0) != null && root.getChildAt(0).equals(adContainer)) {
                Log.i(TAG, "recyclerview holder already has the view no need to change it");
                return;
            }
            Log.i(TAG, "recyclerview just reusing holder, changing content");

            root.removeAllViews();
            if (adContainer.getParent() != null) {
                ((ViewGroup) adContainer.getParent()).removeView(adContainer);
            }

            root.addView(adContainer);
        } else {
            Log.e("ADM_", "getAdapterPosition:" + getAdapterPosition() + " lay:" + getLayoutPosition());
        }


    }
}
