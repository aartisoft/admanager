package com.admanager.core;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    private static boolean exist(CopyOnWriteArrayList<Boolean> list, boolean check) {
        for (int i = 0; i < list.size(); i++) {
            boolean b = list.get(i);
            if (check == b) {
                return true;
            }
        }
        return false;
    }

    public static boolean anyFalse(CopyOnWriteArrayList<Boolean> list) {
        return exist(list, false);
    }

    public static boolean anyTrue(CopyOnWriteArrayList<Boolean> list) {
        return exist(list, true);
    }

    public static boolean allTrue(CopyOnWriteArrayList<Boolean> list) {
        return !exist(list, false);
    }

    public static boolean allFalse(CopyOnWriteArrayList<Boolean> list) {
        return !exist(list, true);
    }

}
