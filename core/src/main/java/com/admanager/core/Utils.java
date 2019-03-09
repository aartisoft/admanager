package com.admanager.core;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {
    public static final String TAG = "Utils";

    // context - START
    public static boolean isContextInvalid(Activity context) {
        boolean destroyed = false;
        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            destroyed = context.isDestroyed();
        }
        return context == null || destroyed || context.isFinishing();
    }

    public static boolean isContextInvalid(Fragment context) {
        return context == null || context.isRemoving();
    }

    public static boolean isContextInvalid(Context context) {
        return context == null;
    }
    // context - END

    public static void hideKeyboard(Activity activity) {
        if (isContextInvalid(activity)) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // screen - START
    public static float dpToPx(Context context, float dp) {
        if (isContextInvalid(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float pxToDp(Context context, int px) {
        if (isContextInvalid(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static boolean isXLargeTablet(Context context) {
        if (isContextInvalid(context)) {
            return false;
        }
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    // screen - END

    // url intent- START
    public static void openLink(Context context, String url) {
        if (isContextInvalid(context)) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "openLink: url is null");
            return;
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception ignored) {

        }
    }

    public static void openApp(Context context, String packageName) {
        if (isContextInvalid(context)) {
            return;
        }
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "openApp: packageName is null");
            return;
        }

        if (packageName.startsWith("http://play.google.com/store/apps/details?id=")) {
            packageName = packageName.replace("http://play.google.com/store/apps/details?id=", "");
        } else if (packageName.startsWith("https://play.google.com/store/apps/details?id=")) {
            packageName = packageName.replace("https://play.google.com/store/apps/details?id=", "");
        } else if (packageName.startsWith("market://details?id=")) {
            packageName = packageName.replace("market://details?id=", "");
        } else if (packageName.startsWith("http")) {
            // not includes packname
            openLink(context, packageName);
            return;
        }

        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
            } catch (Exception ignored) {

            }
        }
    }
    // url intent- END

    // ArrayList - START
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
    // ArrayList - END

    // Connection - START
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnected(Context context) {
        if (isContextInvalid(context)) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnectedWifi(Context context) {
        if (isContextInvalid(context)) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnectedMobile(Context context) {
        if (isContextInvalid(context)) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
    // Connection - END

    // Assets - start
    public static JSONObject loadAssetsJSON(Context context, String filename) {
        if (isContextInvalid(context)) {
            return null;
        }
        String json = loadAssetsFile(context, filename);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // todo checkIfPermissionGranted

    public static String loadAssetsFile(Context context, String filename) {
        if (isContextInvalid(context)) {
            return null;
        }
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            final int read = is.read(buffer);
            is.close();
            if (read > 0) {
                return new String(buffer, Charset.forName("UTF-8"));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    // Assets - END

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String capitalize(@NonNull String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        String[] words = input.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (i > 0 && word.length() > 0) {
                builder.append(" ");
            }

            String cap = word.substring(0, 1).toUpperCase() + word.substring(1);
            builder.append(cap);
        }
        return builder.toString();
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static boolean isActivityEquals(Activity activity, Class<? extends Activity> activity2) {
        if (activity2 == null) {
            return false;
        }
        return activity != null && activity.getClass().getName().equals(activity2.getName());
    }
}
