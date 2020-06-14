package com.admanager.musicplayer.utilities;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public static List<String> getExternalStorage() {
        ArrayList<String> arrayList = new ArrayList<>();
        File file = new File("/storage");
        if (file.exists() && file.isDirectory() && file.listFiles() != null) {
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    File[] listFiles2 = file2.listFiles();
                    if (!(listFiles2 == null || listFiles2.length == 0)) {
                        arrayList.add(file2.getPath());
                    }
                }
            }
        }
        return arrayList;
    }

    public static String getInternalStorage() {
        String folder = System.getenv("EXTERNAL_STORAGE");
        if (TextUtils.isEmpty(folder)) {
            folder = "/storage/sdcard0";
        }

        return folder;
    }

    public static boolean equalsFileList(String[] fileList1, String[] fileList2) {
        if (fileList1 == null && fileList2 == null) {
            return true;
        }
        if ((fileList1 == null && fileList2 != null) || ((fileList1 != null && fileList2 == null) || fileList1.length != fileList2.length)) {
            return false;
        }
        List asList = Arrays.asList(fileList1);
        List asList2 = Arrays.asList(fileList2);
        Collections.sort(asList);
        Collections.sort(asList2);
        return asList.equals(asList2);
    }
}
