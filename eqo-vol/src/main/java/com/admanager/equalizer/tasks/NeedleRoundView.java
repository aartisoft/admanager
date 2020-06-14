package com.admanager.equalizer.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.admanager.equalizer.R;
import com.admanager.equalizer.listeners.NeedleChangedListener;

public class NeedleRoundView extends View {
    public static final float MIN_DEGREE = 45.0f;
    public static final float MAX_DEGREE = 315.0f;

    private final Bitmap bitmapNeedle = BitmapFactory.decodeResource(getResources(), R.drawable.volume_btn);
    private final Matrix matrix = new Matrix();
    private final PaintFlagsDrawFilter paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, 3);
    private float degree = 45.0f;
    private boolean enabled = true;
    private int bitmapWidth;
    private int bitmapHeight;
    private double startAngle = 270.0f;
    private int centerX, centerY;
    private RectF rectF;
    private RectF rectFBitmap;
    private NeedleChangedListener needleChangedListener;

    public NeedleRoundView(Context context) {
        super(context);
        init(context, null);
    }

    public NeedleRoundView(Context context, AttributeSet foo) {
        super(context, foo);
        if (!isInEditMode()) {
            init(context, foo);
        }
    }

    private void init(Context context, AttributeSet foo) {
        bitmapWidth = bitmapNeedle.getWidth();
        bitmapHeight = bitmapNeedle.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        matrix.setRectToRect(rectFBitmap, rectF, Matrix.ScaleToFit.CENTER);

        matrix.postRotate(degree, centerX, centerY);

        canvas.setDrawFilter(paintFlagsDrawFilter);

        canvas.drawBitmap(bitmapNeedle, matrix, null);

        super.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.enabled) {
            return false;
        }

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:

                startAngle = getAngle(centerX, centerY, motionEvent.getX(), motionEvent.getY());

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:

                double currentAngle = getAngle(centerX, centerY, motionEvent.getX(), motionEvent.getY());

                float oldDegree = degree;

                float degreeRotation = ((float) (startAngle - currentAngle));

                if (degreeRotation > 270) {
                    degreeRotation = degreeRotation - 360;
                } else if (degreeRotation < -270) {
                    degreeRotation = 360 + degreeRotation;
                }

                degree = degree + degreeRotation;

                if (degree < MIN_DEGREE) degree = MIN_DEGREE;
                else if (degree > MAX_DEGREE) degree = MAX_DEGREE;

                startAngle = currentAngle;

                if (oldDegree <= MIN_DEGREE && degree <= MIN_DEGREE) {
                    degree = MIN_DEGREE;
                    break;
                }
                if (oldDegree >= MAX_DEGREE && degree >= MAX_DEGREE) {
                    degree = MAX_DEGREE;
                    break;
                }

                if (needleChangedListener != null)
                    needleChangedListener.needleChanged(degree);

                postInvalidate();

                break;
        }

        return true;
    }

    /**
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    private double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec < heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }
    }

    public void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        this.centerX = w / 2;
        this.centerY = h / 2;

        rectF = new RectF(0.0f, 0.0f, w, h);
        rectFBitmap = new RectF(0.0f, 0.0f, (float) this.bitmapWidth, (float) this.bitmapHeight);
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float deg) {
        this.degree = deg;

        if (this.degree < MIN_DEGREE) {
            this.degree = MIN_DEGREE;
        } else if (this.degree > MAX_DEGREE) {
            this.degree = MAX_DEGREE;
        }

        startAngle = startAngle - degree;

        postInvalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setNeedleChangedListener(NeedleChangedListener needleChangedListener) {
        this.needleChangedListener = needleChangedListener;
    }
}
