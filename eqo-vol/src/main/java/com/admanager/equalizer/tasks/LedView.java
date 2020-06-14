package com.admanager.equalizer.tasks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.admanager.equalizer.R;

public class LedView extends View {
    private final int VIEW_LEFT_RIGHT_PADDING = 12;
    private final int VIEW_TOP_BOTTOM_PADDING = 4;

    private final int COLUMN_NUMBER = 2;
    private final int ROW_NUMBER = 16;

    private final int LEFT_RIGHT_PADDING = 6;
    private final int TOP_BOTTOM_PADDING = 6;

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
        Paint lightPaint_1 = new Paint();
        lightPaint_1.setColor(context.getResources().getColor(R.color.light_1));

        Paint lightPaint_2 = new Paint();
        lightPaint_2.setColor(context.getResources().getColor(R.color.light_2));

        Paint lightPaint_3 = new Paint();
        lightPaint_3.setColor(context.getResources().getColor(R.color.light_3));

        Paint lightPaint_4 = new Paint();
        lightPaint_4.setColor(context.getResources().getColor(R.color.light_4));

        Paint lightPaint_5 = new Paint();
        lightPaint_5.setColor(context.getResources().getColor(R.color.light_5));

        //Dark Paints
        Paint darkBlackPaint = new Paint();
        darkBlackPaint.setColor(context.getResources().getColor(R.color.dark_black));

        light_paints = new Paint[]{lightPaint_1, lightPaint_1,
                lightPaint_2, lightPaint_2,
                lightPaint_3, lightPaint_3, lightPaint_3,
                lightPaint_4, lightPaint_4, lightPaint_4, lightPaint_4,
                lightPaint_5, lightPaint_5, lightPaint_5, lightPaint_5, lightPaint_5};

        dark_paints = new Paint[]{darkBlackPaint, darkBlackPaint,
                darkBlackPaint, darkBlackPaint,
                darkBlackPaint, darkBlackPaint, darkBlackPaint,
                darkBlackPaint, darkBlackPaint, darkBlackPaint, darkBlackPaint,
                darkBlackPaint, darkBlackPaint, darkBlackPaint, darkBlackPaint, darkBlackPaint};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewHeight = getMeasuredHeight();
        int viewWidth = getMeasuredWidth();

        int totalColorHeight = viewHeight - ((ROW_NUMBER + 1) * (TOP_BOTTOM_PADDING * 2)) - (VIEW_TOP_BOTTOM_PADDING * 2);
        int height_interval = totalColorHeight / ROW_NUMBER;

        int totalColorWidth = viewWidth - ((COLUMN_NUMBER + 1) * (LEFT_RIGHT_PADDING * 2)) - (VIEW_LEFT_RIGHT_PADDING * 2);
        int width_interval = totalColorWidth / COLUMN_NUMBER;

        //Draw Dark Paints
        int left = 0;
        int right;

        for (int j = 0; j < COLUMN_NUMBER; j++) {
            if (j == 0) {
                left = LEFT_RIGHT_PADDING * 2 + VIEW_LEFT_RIGHT_PADDING;
            } else {
                left = left + LEFT_RIGHT_PADDING * 2 + width_interval;
            }

            right = left + width_interval;

            int top = 0;
            int bottom;

            for (int i = 0; i < ROW_NUMBER; i++) {
                if (i == 0) {
                    top = TOP_BOTTOM_PADDING * 2 + VIEW_TOP_BOTTOM_PADDING;
                } else {
                    top = top + TOP_BOTTOM_PADDING * 2 + height_interval;
                }

                bottom = top + height_interval;

                canvas.drawCircle((left + right) / 2, (top + bottom) / 2, (width_interval) / 2, dark_paints[i]);
            }
        }

        //Draw Light Paints
        left = 0;

        for (int j = 0; j < COLUMN_NUMBER; j++) {
            if (j == 0) {
                left = LEFT_RIGHT_PADDING * 2 + VIEW_LEFT_RIGHT_PADDING;
            } else {
                left = left + LEFT_RIGHT_PADDING * 2 + width_interval;
            }

            right = left + width_interval;

            int top = 0;
            int bottom;

            if (level > 0) {
                top = TOP_BOTTOM_PADDING * 2 + VIEW_TOP_BOTTOM_PADDING;
                top = top + (TOP_BOTTOM_PADDING * 2 + height_interval) * (ROW_NUMBER - level - 1);
            }

            for (int i = ROW_NUMBER - level; i < ROW_NUMBER; i++) {
                if (i == 0) {
                    top = TOP_BOTTOM_PADDING * 2 + VIEW_TOP_BOTTOM_PADDING;
                } else {
                    top = top + TOP_BOTTOM_PADDING * 2 + height_interval;
                }

                bottom = top + height_interval;

                canvas.drawCircle((left + right) / 2, (top + bottom) / 2, (width_interval) / 2, light_paints[i]);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int totalColorHeight = heightMeasureSpec - ((ROW_NUMBER + 1) * (TOP_BOTTOM_PADDING * 2));
        int oneColorHeight = totalColorHeight / ROW_NUMBER;
        int newHeightMeasureSpec = oneColorHeight * ROW_NUMBER + ((ROW_NUMBER + 1) * (TOP_BOTTOM_PADDING * 2));

        int rationalWidthtMeasureSpec = ((newHeightMeasureSpec / ROW_NUMBER) * COLUMN_NUMBER);

        int totalColorWidth = rationalWidthtMeasureSpec - ((COLUMN_NUMBER + 1) * (LEFT_RIGHT_PADDING * 2));
        int oneColorWidth = totalColorWidth / COLUMN_NUMBER;
        int newWidthtMeasureSpec = oneColorWidth * COLUMN_NUMBER + ((COLUMN_NUMBER + 1) * (LEFT_RIGHT_PADDING * 2));

        setMeasuredDimension(newWidthtMeasureSpec + (VIEW_LEFT_RIGHT_PADDING * 2), newHeightMeasureSpec + (VIEW_TOP_BOTTOM_PADDING * 2));
    }

    public void setLevelNumber(int levelNo) {
        this.level = levelNo;

        postInvalidate();
    }
}
