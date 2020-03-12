package com.admanager.unseen.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.admanager.unseen.R;

/**
 * Created by a on 1.02.2017.
 */
class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
    }

    public void bindTo() {
        progressBar.setIndeterminate(true);
    }
}
