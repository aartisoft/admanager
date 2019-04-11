package com.admanager.applocker.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.admanager.applocker.data.AppInfo;
import com.admanager.applocker.fragments.AllAppFragment;
import com.admanager.applocker.prefrence.Prefs;
import com.admanager.applocker.utils.AppLockConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GetListOfAppsAsyncTask extends AsyncTask<String, Void, List<AppInfo>> {

    AllAppFragment container;

    public GetListOfAppsAsyncTask(AllAppFragment fragment) {
        container = fragment;
    }

    /**
     * get the list of all installed applications in the device
     *
     * @return ArrayList of installed applications or null
     */
    public static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> installedApps = new ArrayList<>();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        if (apps != null && !apps.isEmpty()) {

            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                try {
                    if (null != packageManager.getLaunchIntentForPackage(p.packageName)) {
                        AppInfo app = new AppInfo();
                        app.setName(p.applicationInfo.loadLabel(packageManager).toString());
                        app.setPackageName(p.packageName);
                        app.setVersionName(p.versionName);
                        app.setVersionCode(p.versionCode);
                        app.setIcon(p.applicationInfo.loadIcon(packageManager));

                        installedApps.add(app);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return installedApps;
        }
        return null;
    }

    @Override
    protected List<AppInfo> doInBackground(String... strings) {

        String requiredAppsType = strings[0];

        List<AppInfo> list = getListOfInstalledApp(container.getActivity());

        List<AppInfo> lockedFilteredAppList = new ArrayList<>();
        List<AppInfo> unlockedFilteredAppList = new ArrayList<>();
        if (requiredAppsType.matches(AppLockConstants.LOCKED) || requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
            Set<String> set = Prefs.with(container.getActivity()).getLocked();
            for (int i = 0; i < list.size(); i++) {
                AppInfo appInfo = list.get(i);
                if (set != null && set.contains(appInfo.getPackageName())) {
                    lockedFilteredAppList.add(appInfo);
                } else {
                    unlockedFilteredAppList.add(appInfo);
                }
            }
            if (requiredAppsType.matches(AppLockConstants.LOCKED)) {
                return lockedFilteredAppList;
            } else if (requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
                return unlockedFilteredAppList;
            }

        }
        return list;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        container.showProgressBar();
    }

    @Override
    protected void onPostExecute(List<AppInfo> appInfos) {
        super.onPostExecute(appInfos);

        if (container != null && container.getActivity() != null) {
            container.hideProgressBar();
            container.updateData(appInfos);
        }

    }
}
