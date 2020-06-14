package com.admanager.musicplayer.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.admanager.musicplayer.R;

public class RingRoundView extends View {
    public static final float MIN_DEGREE = 45.0f;
    public static final float MAX_DEGREE = 315.0f;

    private final Bitmap bitmapRing = BitmapFactory.decodeResource(getResources(), R.drawable.mp_volume_colored);

    private float degree = 45.0f;

    private RectF rectF;

    private int bitmapWidth;
    private int bitmapHeight;

    private int width;
    private int height;

    private Paint paint;

    public RingRoundView(Context context) {
        super(context);
        init(context, null);
    }

    public RingRoundView(Context context, AttributeSet foo) {
        super(context, foo);
        if (!isInEditMode()) {
            init(context, foo);
        }
    }

    private void init(Context context, AttributeSet foo) {
        bitmapWidth = bitmapRing.getWidth();
        bitmapHeight = bitmapRing.getHeight();

        this.rectF = new RectF();

        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isEnabled()) {
            canvas.drawArc(rectF, 135, degree - MIN_DEGREE, true, paint);
            return;
        }

        canvas.drawArc(rectF, 135, 0, true, paint);
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
        this.width = w;
        this.height = h;

        adjustMatrix();
    }

    private void adjustMatrix() {
        Matrix matrix = new Matrix();

        RectF bitMapRectf = new RectF(0.0f, 0.0f, (float) this.bitmapWidth, (float) this.bitmapHeight);
        matrix.setRectToRect(bitMapRectf, new RectF(0.0f, 0.0f, this.width, this.height), Matrix.ScaleToFit.CENTER);

        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader = new BitmapShader(bitmapRing, tileMode, tileMode);
        bitmapShader.setLocalMatrix(matrix);
        this.paint.setShader(bitmapShader);
        this.paint.setAntiAlias(true);

        matrix.mapRect(rectF, bitMapRectf);
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

        postInvalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
}
