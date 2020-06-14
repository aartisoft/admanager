package com.admanager.equalizer.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {
    public SquareImageView(final Context context) {
        super(context);
    }

    public SquareImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(final Context context, final AttributeSet attrs,
                           final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int width, int height) {
        if (width < height) {
            super.onMeasure(width, width);

            setMeasuredDimension(width, width);
        } else {
            super.onMeasure(height, height);

            setMeasuredDimension(height, height);
        }
    }
}

