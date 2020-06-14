package com.admanager.equalizer.utilities;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Jerry on 1/22/2018.
 */

public class ExternalStorageUtil {

    // Check whether the external storage is mounted or not.
    public static boolean isExternalStorageMounted() {

        String dirState = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(dirState);
    }

    // Check whether the external storage is read only or not.
    public static boolean isExternalStorageReadOnly() {

        String dirState = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(dirState);
    }

    // Get private external storage base directory.
    public static String getPrivateExternalStorageBaseDir(Context context, String dirType) {
        String ret = "";
        if (isExternalStorageMounted()) {
            File file = context.getExternalFilesDir(dirType);
            if (file != null) {
                ret = file.getAbsolutePath();
            }
        }
        return ret;
    }

    // Get private cache external storage base directory.
    public static String getPrivateCacheExternalStorageBaseDir(Context context) {
        String ret = "";
        if (isExternalStorageMounted()) {
            File file = context.getExternalCacheDir();
            if (file != null) {
                ret = file.getAbsolutePath();
            }
        }
        return ret;
    }


    // Get public external storage base directory.
    public static String getPublicExternalStorageBaseDir() {
        String ret = "";
        if (isExternalStorageMounted()) {
            File file = Environment.getExternalStorageDirectory();
            ret = file.getAbsolutePath();
        }
        return ret;
    }

    // Get public external storage base directory.
    public static String getPublicExternalStorageBaseDir(String dirType) {
        String ret = "";
        if (isExternalStorageMounted()) {
            File file = Environment.getExternalStoragePublicDirectory(dirType);
            ret = file.getAbsolutePath();
        }
        return ret;
    }
}
