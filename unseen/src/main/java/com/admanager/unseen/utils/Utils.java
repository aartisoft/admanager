package com.admanager.unseen.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.provider.Settings;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.admanager.unseen.R;
import com.admanager.unseen.notiservice.NotiListenerService;
import com.admanager.unseen.notiservice.converters.ConverterData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by a on 28.01.2017.
 */

public class Utils {

    private static String TAG = "Utils";

    public static void setCornerRadiusBgColorToImageView(ImageView img, String type) {
        ConverterData data = NotiListenerService.getConverterData().get(type);
        if (data == null) {
            return;
        }
        setCornerRadiusBgColorToImageView(img, data.getColor());
    }

    public static void setCornerRadiusBgColorToImageView(ImageView img, @ColorRes int color) {
        if (color == -1) {
            return;
        }
        GradientDrawable gd = createGradientDrawable(img.getContext(), color, 200);
        img.setBackground(gd);
    }

    public static GradientDrawable createGradientDrawable(Context context, @ColorRes int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        int c = ContextCompat.getColor(context, color);

        gd.setColor(c);
        gd.setStroke(2, c);
        gd.setShape(radius <= 0 ? GradientDrawable.RECTANGLE : GradientDrawable.OVAL);
        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        if (radius > 0) {
            gd.setGradientRadius(radius);
        }
        return gd;
    }

    public static String getStringFormattedDate(Context context, long time) {
        if (time == 0L) {
            return "";
        }
        Calendar tc = Calendar.getInstance();
        tc.setTime(new Date(time));
        Calendar now = Calendar.getInstance();

        long delta = Math.abs(now.getTimeInMillis() - tc.getTimeInMillis()) / 1000;

        if (delta < 86400) // 24 * 60 * 60
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            return simpleDateFormat.format(tc.getTime());
        } else {
            delta /= (60.0 * 60.0 * 24.0); // convert seconds to days
            return (int) delta + " " + context.getResources().getString(R.string.timeago_days);
        }
    }

    public static String shortenText(String txt) {
        if (txt == null || "".equals(txt.trim())) {
            return null;
        }

        if (txt.length() < 36) {
            return txt;
        }

        return String.format("%s...", txt.substring(0, 36));
    }

    public static boolean checkNotificationEnabled(Context context) {
        try {
            String string = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
            CharSequence packageName = context.getPackageName();
            return !(string == null || !string.contains(packageName));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}