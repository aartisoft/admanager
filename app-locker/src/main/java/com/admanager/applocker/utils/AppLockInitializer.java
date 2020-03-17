package com.admanager.applocker.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.admanager.applocker.activities.PermissionGuideActivity;
import com.admanager.applocker.fragments.PermissionsFragment;
import com.admanager.applocker.prefrence.Prefs;
import com.admanager.applocker.services.AppCheckServices;

public class AppLockInitializer {
    public static final int REQ_CODE_PASSWORD_TYPE = 100;
    public static final int REQ_CODE_PASSWORD = 101;
    public static final int REQ_CODE_USAGE_STATS_PERM = 103;

    // context - START
    private static boolean isContextInvalid(Activity context) {
        boolean destroyed = false;
        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            destroyed = context.isDestroyed();
        }
        return context == null || destroyed || context.isFinishing();
    }

    public static void onActivityResult(AppCompatActivity activity) {
        initAndForcePermissionDialog(activity);
    }

    public static void init(Application application) {
        if (application == null) {
            return;
        }
        AppCheckServices.startServiceAndSetAlarm(application);
    }

    public static void initAndForcePermissionDialog(AppCompatActivity activity) {
        if (isContextInvalid(activity)) {
            return;
        }
        AppCheckServices.startServiceAndSetAlarm(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !isPermissionsGranted(activity)) {
            PermissionsFragment fragment = new PermissionsFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragment.show(fragmentManager, "perm_dialog");
        }
    }

    public static boolean isPermissionsGranted(Context context) {
        boolean hasUsageAccess = hasUsageStatsPermission(context);
        boolean passwordSet = Prefs.with(context).isPasswordSet();

        return hasUsageAccess && passwordSet;
    }

    public static void goToUsageAccessPermissionSettings(final Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        try {
            activity.startActivityForResult(intent, REQ_CODE_USAGE_STATS_PERM);
        } catch (Exception e) {
            //  not exported from uid 1000
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isContextInvalid(activity)) {
                    return;
                }
                activity.startActivity(new Intent(activity, PermissionGuideActivity.class));
            }
        }, 3000);
    }

    public static boolean hasUsageStatsPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

}
