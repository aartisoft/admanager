package com.admanager.colorcallscreen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullScreenVideoView extends VideoView {
    private boolean fullscreen = true;

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FullScreenVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        //        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.admanager.colorcallscreen.view.FullScreenVideoView);
        //        this.fullscreen = obtainStyledAttributes.getBoolean(0, false);
        //        obtainStyledAttributes.recycle();
    }

    protected void onMeasure(int i, int i2) {
        if (this.fullscreen) {
            setMeasuredDimension(getDefaultSize(0, i), getDefaultSize(0, i2));
        } else {
            super.onMeasure(i, i2);
        }
    }

}