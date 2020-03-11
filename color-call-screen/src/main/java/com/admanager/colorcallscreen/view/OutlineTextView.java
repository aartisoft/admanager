package com.admanager.colorcallscreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

public class OutlineTextView extends TextView {
    public OutlineTextView(Context context) {
        super(context);
    }

    // Constructors

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            super.draw(canvas);
        }
    }

}