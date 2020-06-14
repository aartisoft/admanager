package com.admanager.musicplayer.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.fragment.app.Fragment;

public class ContextUtils {
    public static boolean isContextValid(Activity context) {
        boolean destroyed = false;
        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            destroyed = context.isDestroyed();
        }
        return context != null && !destroyed && !context.isFinishing();
    }

    public static boolean isContextValid(Fragment context) {
        return context != null && !context.isRemoving();
    }

    public static boolean isContextValid(Context context) {
        return context != null;
    }
}
