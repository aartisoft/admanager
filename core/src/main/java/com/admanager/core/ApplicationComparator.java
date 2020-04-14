package com.admanager.core;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public abstract class ApplicationComparator<T> {

    public abstract String getPackageNameFromModel(T model);

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getLaunchIntentForPackage(packageName) != null;
        } catch (Throwable e) {
            return false;
        }
    }

    public List<T> sortByExistence(Context activity, List<T> list) {
        List<T> orderedList = new ArrayList<>();
        PackageManager pm = activity.getPackageManager();
        appendToList(orderedList, list, pm, true); // Append existing applications
        appendToList(orderedList, list, pm, false); // Append the other applications

        return orderedList;
    }

    private void appendToList(List<T> orderedList, List<T> list, PackageManager pm, boolean existence) {
        for (T o : list) {
            String packageName = getPackageNameFromModel(o);

            if (isPackageInstalled(packageName, pm) == existence) {
                orderedList.add(o);
            }
        }
    }
}


