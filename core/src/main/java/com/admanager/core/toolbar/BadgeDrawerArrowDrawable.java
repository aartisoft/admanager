package com.admanager.core.toolbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

public class BadgeDrawerArrowDrawable extends DrawerArrowDrawable {

    // Fraction of the drawable's intrinsic size we want the badge to be.

    private Paint mBadgePaint;
    private Paint mTextPaint;
    private float mTextSize;
    private String mCount;
    private boolean enabled = true;
    private Rect mTxtRect = new Rect();

    public BadgeDrawerArrowDrawable(Context context) {
        super(context);

        mTextSize = dpToPx(context, 8); //text size
        mBadgePaint = new Paint();
        mBadgePaint.setColor(Color.RED);
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Only draw a badge if there are notifications.
        if (mCount.equalsIgnoreCase("0")) {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;
        // Position the badge in the top-right quadrant of the icon.
        /*Using Math.max rather than Math.min */
//  float radius = ((Math.max(width, height) / 2)) / 2;
        float radius = width * 0.15f;
        float centerX = (width - radius - 1) + 10;
        float centerY = radius - 5;
        if (mCount.length() <= 2) {
            // Draw badge circle.
            canvas.drawCircle(centerX, centerY, radius + 7, mBadgePaint);
        } else {
            canvas.drawCircle(centerX, centerY, radius + 8, mBadgePaint);
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = centerY + (textHeight / 2f);
        if (mCount.length() > 2) {
            canvas.drawText("99+", centerX, textY, mTextPaint);
        } else {
            canvas.drawText(mCount, centerX, textY, mTextPaint);
        }
    }

    private float dpToPx(Context context, float value) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics());
        return px;
    }

    /*
     Sets the count (i.e notifications) to display.
      */

    @Override
    public void setAlpha(int alpha) {
        // do nothing
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            invalidateSelf();
        }
    }

    public String getText() {
        return mCount;
    }

    public void setText(String text) {
        if (this.mCount == null || !this.mCount.equals(text)) {
            this.mCount = text;
            invalidateSelf();
        }
    }

    public int getBackgroundColor() {
        return mBadgePaint.getColor();
    }

    public void setBackgroundColor(int color) {
        if (mBadgePaint.getColor() != color) {
            mBadgePaint.setColor(color);
            invalidateSelf();
        }
    }

    public int getTextColor() {
        return mTextPaint.getColor();
    }

    public void setTextColor(int color) {
        if (mTextPaint.getColor() != color) {
            mTextPaint.setColor(color);
            invalidateSelf();
        }
    }
}