package com.admanager.musicplayer.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

public class HorizontalSeekBar extends AppCompatSeekBar {
    private final Rect bounds = new Rect();

    private final Paint paint = new Paint();

    private boolean pressed = false;

    public HorizontalSeekBar(Context context) {
        super(context);
    }

    public HorizontalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HorizontalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    protected void onDraw(Canvas c) {
        super.onDraw(c);

        if (pressed) {
            int max = getMax();

            int thumb_y = (int) (double) this.getHeight() / 2;

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
            paint.setTextSize(36);

            String text = "" + (int) ((getProgress() * 1.0f / max) * 100.0f);

            paint.getTextBounds(text, 0, text.length(), bounds);

            float middle = (getWidth() / 2.0f) - (bounds.width() / 2.0f);

            c.drawText(text, middle, thumb_y + (bounds.height() / 2.0f), paint);
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
                setProgress((int) (getMax() * event.getX() / getWidth()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
            case MotionEvent.ACTION_UP:
                pressed = false;
                super.setProgress((int) (getMax() * event.getX() / getWidth()));
                setProgress((int) (getMax() * event.getX() / getWidth()));
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