package com.admanager.musicplayer.tasks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.admanager.musicplayer.R;

public class LedView extends View {
    private final int columnNumber = 2;
    private final int rowNumber = 16;

    private final int left_right_padding = 4;
    private final int top_bottom_padding = 4;

    /*private Paint lightRedPaint, lightOrangePaint, lightYellowPaint, lightBluePaint, lightGreenPaint;
    private Paint darkRedPaint, darkOrangePaint, darkYellowPaint, darkBluePaint, darkGreenPaint;*/

    /* renamed from: i */
    private int[] light_colors = {R.color.mplight_red, R.color.mplight_red,
            R.color.mplight_orange, R.color.mplight_orange,
            R.color.mplight_yellow, R.color.mplight_yellow, R.color.mplight_yellow,
            R.color.mplight_blue, R.color.mplight_blue, R.color.mplight_blue, R.color.mplight_blue,
            R.color.mplight_green, R.color.mplight_green, R.color.mplight_green, R.color.mplight_green, R.color.mplight_green};

    /* renamed from: j */
    private int[] dark_colors = {R.color.mpdark_red, R.color.mpdark_red,
            R.color.mpdark_orange, R.color.mpdark_orange,
            R.color.mpdark_yellow, R.color.mpdark_yellow, R.color.mpdark_yellow,
            R.color.mpdark_blue, R.color.mpdark_blue, R.color.mpdark_blue, R.color.mpdark_blue,
            R.color.mpdark_green, R.color.mpdark_green, R.color.mpdark_green, R.color.mpdark_green, R.color.mpdark_green};

    private Paint[] light_paints;
    private Paint[] dark_paints;

    private int level = 0;

    public LedView(Context context) {
        super(context);
        init(context, null);
    }

    public LedView(Context context, AttributeSet foo) {
        super(context, foo);
        if (!isInEditMode()) {
            init(context, foo);
        }
    }

    private void init(Context context, AttributeSet foo) {
        //Light Paints
        Paint lightRedPaint = new Paint();
        lightRedPaint.setColor(context.getResources().getColor(R.color.mplight_red));

        Paint lightOrangePaint = new Paint();
        lightOrangePaint.setColor(context.getResources().getColor(R.color.mplight_orange));

        Paint lightYellowPaint = new Paint();
        lightYellowPaint.setColor(context.getResources().getColor(R.color.mplight_yellow));

        Paint lightBluePaint = new Paint();
        lightBluePaint.setColor(context.getResources().getColor(R.color.mplight_blue));

        Paint lightGreenPaint = new Paint();
        lightGreenPaint.setColor(context.getResources().getColor(R.color.mplight_green));

        //Dark Paints
        Paint darkRedPaint = new Paint();
        darkRedPaint.setColor(context.getResources().getColor(R.color.mpdark_red));

        Paint darkOrangePaint = new Paint();
        darkOrangePaint.setColor(context.getResources().getColor(R.color.mpdark_orange));

        Paint darkYellowPaint = new Paint();
        darkYellowPaint.setColor(context.getResources().getColor(R.color.mpdark_yellow));

        Paint darkBluePaint = new Paint();
        darkBluePaint.setColor(context.getResources().getColor(R.color.mpdark_blue));

        Paint darkGreenPaint = new Paint();
        darkGreenPaint.setColor(context.getResources().getColor(R.color.mpdark_green));

        light_paints = new Paint[]{lightRedPaint, lightRedPaint,
                lightOrangePaint, lightOrangePaint,
                lightYellowPaint, lightYellowPaint, lightYellowPaint,
                lightBluePaint, lightBluePaint, lightBluePaint, lightBluePaint,
                lightGreenPaint, lightGreenPaint, lightGreenPaint, lightGreenPaint, lightGreenPaint};

        dark_paints = new Paint[]{darkRedPaint, darkRedPaint,
                darkOrangePaint, darkOrangePaint,
                darkYellowPaint, darkYellowPaint, darkYellowPaint,
                darkBluePaint, darkBluePaint, darkBluePaint, darkBluePaint,
                darkGreenPaint, darkGreenPaint, darkGreenPaint, darkGreenPaint, darkGreenPaint};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewHeight = getMeasuredHeight();
        int viewWidth = getMeasuredWidth();

        int totalColorHeight = viewHeight - ((rowNumber + 1) * (top_bottom_padding * 2));
        int height_interval = totalColorHeight / rowNumber;

        int totalColorWidth = viewWidth - ((columnNumber + 1) * (left_right_padding * 2));
        int width_interval = totalColorWidth / columnNumber;

        //Draw Dark Paints
        int left = 0;
        int right;

        for (int j = 0; j < columnNumber; j++) {
            if (j == 0) {
                left = left_right_padding * 2;
            } else {
                left = left + left_right_padding * 2 + width_interval;
            }

            right = left + width_interval;

            int top = 0;
            int bottom;

            for (int i = 0; i < rowNumber; i++) {
                if (i == 0) {
                    top = top_bottom_padding * 2;
                } else {
                    top = top + top_bottom_padding * 2 + height_interval;
                }

                bottom = top + height_interval;

                canvas.drawRect(left, top, right, bottom, dark_paints[i]);
            }
        }

        //Draw Light Paints
        left = 0;

        for (int j = 0; j < columnNumber; j++) {
            if (j == 0) {
                left = left_right_padding * 2;
            } else {
                left = left + left_right_padding * 2 + width_interval;
            }

            right = left + width_interval;

            int top = 0;
            int bottom;

            if (level > 0) {
                top = top_bottom_padding * 2;
                top = top + (top_bottom_padding * 2 + height_interval) * (rowNumber - level - 1);
            }

            for (int i = rowNumber - level; i < rowNumber; i++) {
                if (i == 0) {
                    top = top_bottom_padding * 2;
                } else {
                    top = top + top_bottom_padding * 2 + height_interval;
                }

                bottom = top + height_interval;

                canvas.drawRect(left, top, right, bottom, light_paints[i]);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int totalColorHeight = heightMeasureSpec - ((rowNumber + 1) * (top_bottom_padding * 2));
        int oneColorHeight = totalColorHeight / rowNumber;
        int newHeightMeasureSpec = oneColorHeight * rowNumber + ((rowNumber + 1) * (top_bottom_padding * 2));

        int rationalWidthtMeasureSpec = (int) ((newHeightMeasureSpec / rowNumber) * 2.0f * columnNumber);

        int totalColorWidth = rationalWidthtMeasureSpec - ((columnNumber + 1) * (left_right_padding * 2));
        int oneColorWidth = totalColorWidth / columnNumber;
        int newWidthtMeasureSpec = oneColorWidth * columnNumber + ((columnNumber + 1) * (left_right_padding * 2));

        setMeasuredDimension(newWidthtMeasureSpec, newHeightMeasureSpec);
    }

    public void setLevelNumber(int levelNo) {
        this.level = levelNo;

        postInvalidate();
    }
}
