package com.admanager.gifs.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionChecker {
    private static final String TAG = "Gifs";
    private static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    private static String[] PERMS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private Runnable runAfterPermission;
    private Activity activity;

    public PermissionChecker(Activity activity) {
        this.activity = activity;
    }

    public void checkPermissionGrantedAndRun(@NonNull Runnable runnable) {
        boolean preM = Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
        boolean permissionGranted = preM || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            runnable.run();
        } else {
            this.runAfterPermission = runnable;
            checkPermissions(PERMS);
        }
    }

    private boolean checkPermissions(String[] perms) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : perms) {
            result = ContextCompat.checkSelfPermission(activity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(listPermissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS);
            }
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "PERMISSION GRANTED");

                if (runAfterPermission != null) {
                    runAfterPermission.run();
                    runAfterPermission = null;
                }

            } else {
                // no permissions granted.
                Log.i(TAG, "PERMISSION IS NOT GRANTED");
            }
        }
    }
}