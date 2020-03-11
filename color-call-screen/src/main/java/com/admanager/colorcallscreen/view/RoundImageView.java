package com.admanager.colorcallscreen.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RoundImageView extends AppCompatImageView {
    private Paint paint = new Paint(1);
    private Bitmap bitmap;
    private BitmapShader shader;
    private Matrix matrix = new Matrix();

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RoundImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    protected void onDraw(Canvas canvas) {
        Bitmap bitmap;
        int i;
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            Rect bounds = drawable.getBounds();
            int i2 = bounds.right - bounds.left;
            i = bounds.bottom - bounds.top;
            int color = ((ColorDrawable) drawable).getColor();
            bitmap = Bitmap.createBitmap(i2, i, Config.ARGB_8888);
            new Canvas(bitmap).drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
        } else {
            bitmap = null;
        }
        if (bitmap != null) {
            i = Math.min(getWidth(), getHeight());
            if (this.shader == null || !bitmap.equals(this.bitmap)) {
                this.bitmap = bitmap;
                this.shader = new BitmapShader(this.bitmap, TileMode.CLAMP, TileMode.CLAMP);
            }
            this.matrix.setScale((float) (i / bitmap.getWidth()), (float) (i / bitmap.getHeight()));
            this.shader.setLocalMatrix(this.matrix);

            this.paint.setShader(this.shader);
            float f = ((float) i) / 2.0f;
            canvas.drawCircle(f, f, f, this.paint);
            return;
        }
        super.onDraw(canvas);
    }
}