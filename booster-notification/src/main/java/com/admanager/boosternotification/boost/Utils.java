package com.admanager.boosternotification.boost;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import java.text.DecimalFormat;

import static android.content.Context.ACTIVITY_SERVICE;

public class Utils {

    public static Drawable createOvalDrawable(int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setShape(GradientDrawable.OVAL);
        return gradientDrawable;
    }

    public static Drawable createRoundedStrokeDrawable(int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(0x00ffffff);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(28);
        gradientDrawable.setStroke(2, color);
        return gradientDrawable;
    }

    public static long freeRamMemorySize(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.availMem;
    }

    public static long totalRamMemorySize(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        if (activityManager == null) return 0;
        activityManager.getMemoryInfo(mi);
        return mi.totalMem;
    }

    public static int toMB(long diffBytes) {
        return (int) (diffBytes / (1024 * 1024l));
    }

    public static String toGB(long diffBytes) {
        return new DecimalFormat("#,##0.#").format((diffBytes / (1024 * 1024 * 1024d))) + " GB";
    }
}
