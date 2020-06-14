package com.admanager.musicplayer.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

public class VerticalSeekBar extends AppCompatSeekBar {
    private final Rect bounds = new Rect();

    private final Paint paint = new Paint();

    private boolean pressed = false;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    public synchronized void setProgress(int progress) // it is necessary for
    // calling setProgress
    // on click of a button
    {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);

        if (pressed) {
            int max = getMax();

            int thumb_x = (int) (double) this.getWidth() / 2;

            int height = getHeight() - 88;
            float middle = height - (getProgress() * 1.0f / max * 1.0f) * (height);
            if (middle < 44) middle = 44;


            paint.setColor(Color.WHITE);
            paint.setTextSize(40);

            c.save();
            c.rotate(90);
            c.translate(0, -getHeight());

            String text;

            int value = (int) ((getProgress() - (max / 2.0f)) / 100.0f);
            if (value > 0) text = "+" + value;
            else text = "" + value;

            paint.getTextBounds(text, 0, text.length(), bounds);

            c.drawText(text, thumb_x - (bounds.width() / 2.0f), middle, paint);

            c.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                pressed = true;
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
            case MotionEvent.ACTION_UP:
                pressed = false;
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                pressed = false;
                break;
        }
        return true;
    }

    public void updateThumb() {
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}