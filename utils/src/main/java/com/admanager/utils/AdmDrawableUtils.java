package com.admanager.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

public class AdmDrawableUtils {

    public static final int DEFAULT_IMG_SIZE = 150;
    private static final String FOLDER_NAME = "Stickers";

    public static GradientDrawable getRandomColoredDrawable() {
        return getRandomColoredDrawable(null, null);
    }

    public static GradientDrawable getRandomColoredDrawable(Integer x, Integer y) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        if (x != null && y != null) {
            shape.setSize(x, y);
        }
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        shape.setColor(color);
        shape.setStroke(3, color);
        return shape;
    }


    public static boolean copyDrawableFromImageViewToFile(File gifFile, ImageView image) {
        Drawable.ConstantState constantState = image.getDrawable().getConstantState();
        if (constantState == null) {
            return false;
        }

        Drawable drawable = constantState.newDrawable().mutate();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(gifFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                return true;
            } catch (IOException e) {
                Log.e("app", e.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } else if (drawable instanceof GifDrawable) {
            ByteBuffer byteBuffer2 = ((GifDrawable) drawable).getBuffer();

            try {
                byteBuffer2.rewind();
                byte[] arr = new byte[byteBuffer2.remaining()];
                byteBuffer2.get(arr);
                FileOutputStream output = new FileOutputStream(gifFile);
                output.write(arr, 0, arr.length);
                output.close();
                return true;

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
