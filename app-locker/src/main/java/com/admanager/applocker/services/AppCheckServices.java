package com.admanager.applocker.services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.admanager.applocker.R;
import com.admanager.applocker.activities.PasswordActivity;
import com.admanager.applocker.prefrence.Prefs;
import com.admanager.applocker.utils.AppLockInitializer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AppCheckServices extends Service {

    public static final String TAG = "AppCheckServices";
    public static final int NOTIF_ID = 25478;
    private static Map<String, Long> recentlyUnlockedMap = new ConcurrentHashMap<>();
    private static Map<String, Long> recentlyAskedMap = new ConcurrentHashMap<>();
    long TIMEOUT = 0;
    long AUTO_CLOSE = 0;
    Handler UITHREAD = new Handler(Looper.getMainLooper());
    private String currentApp = "";
    private Prefs prefs;
    private Set<String> packageNamesNeedLock;
    private Timer timer;
    private boolean isScreenOn = true;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            String action = intent.getAction();

            if (action == null) {
                return;
            }

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                updateRecentlyUnlockedApps();
                isScreenOn = false;
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                timeoutRecentlyUnlockedApps();
                isScreenOn = true;
            }
        }
    };
    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            if (prefs != null) {
                packageNamesNeedLock = prefs.getLocked();
            }

            if (packageNamesNeedLock == null || packageNamesNeedLock.isEmpty()) {
                // no need to check anything
                return;
            }

            if (!AppLockInitializer.isPermissionsGranted(getApplicationContext())) {
                // no need to check anything
                return;
            }

            boolean currentLockedAppIfInForeground = isConcernedAppIsInForeground();
            if (!currentLockedAppIfInForeground) {
                return;
            }

            if (!isScreenOn) {
                return;
            }
            UITHREAD.post(new Runnable() {
                public synchronized void run() {
                    boolean newApp = recentlyUnlockedMap.get(currentApp) == null;
                    boolean recentlyAsked = isRecentlyAsked();
                    Log.d(TAG, String.format("%36.36s newApp:" + newApp + " \trecentlyAsked:" + recentlyAsked + "\t\t" + currentApp, ""));

                    if (newApp) {
                        setAsked(currentApp);
                        if (!recentlyAsked) {
                            PasswordActivity.startPasswordAsk(getApplicationContext(), currentApp);
                        }
                    }
                }
            });
        }
    };

    public static void setAsUnlocked(String packageName) {
        AppCheckServices.recentlyUnlockedMap.put(packageName, System.currentTimeMillis());
    }

    public static void setAsked(String packageName) {
        AppCheckServices.recentlyAskedMap.put(packageName, System.currentTimeMillis());
    }

    public static void removedAsked(String packageName) {
        AppCheckServices.recentlyAskedMap.remove(packageName);
    }

    public static void startServiceAndSetAlarm(Context context) {
        Intent intent = new Intent(context, AppCheckServices.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            long six_hours = TimeUnit.HOURS.toMillis(6);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
            manager.cancel(pendingIntent);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), six_hours, pendingIntent);
        }
    }

    private boolean isRecentlyAsked() {
        Long aLong = recentlyAskedMap.get(currentApp);
        long time = aLong == null ? 0 : aLong;
        if (time == 0) {
            return false;
        }
        long diff = System.currentTimeMillis() - time;
        if (diff > 5000) {
            Log.v(TAG, "RECENTLY ASK timeout:" + diff);
            recentlyAskedMap.remove(currentApp);
            return false;
        } else {
            Log.v(TAG, "RECENTLY ASKED:" + diff);
        }
        return true;
    }

    private void timeoutRecentlyUnlockedApps() {
        for (Map.Entry<String, Long> entry : recentlyUnlockedMap.entrySet()) {
            Long unlockedTime = entry.getValue();
            long lastUnlockedTime = unlockedTime != null ? unlockedTime : 0L;
            long timePassed = System.currentTimeMillis() - lastUnlockedTime;

            if (timePassed > TIMEOUT) {
                String pn = entry.getKey();
                recentlyUnlockedMap.remove(pn);
                Log.v(TAG, "removing recent app list: " + pn);
            } else {
                Log.v(TAG, "NOT TIMEOUT: " + (TIMEOUT - timePassed));
            }
        }
    }

    private void updateRecentlyUnlockedApps() {
        for (Map.Entry<String, Long> entry : recentlyUnlockedMap.entrySet()) {
            recentlyUnlockedMap.put(entry.getKey(), System.currentTimeMillis());
        }
    }

    public boolean checkNoNeedToService() {
        if (prefs == null) {
            prefs = Prefs.with(getApplicationContext());
        }
        packageNamesNeedLock = prefs.getLocked();

        if (packageNamesNeedLock == null || packageNamesNeedLock.isEmpty()) {
            // no need to check anything
            return true;
        }

        if (!AppLockInitializer.isPermissionsGranted(getApplicationContext())) {
            // no need to check anything
            return true;
        }

        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground();

        prefs = Prefs.with(getApplicationContext());

        if (!checkNoNeedToService()) {
            packageNamesNeedLock = prefs.getLocked();
            timer = new Timer("AppCheckServices");
            timer.scheduleAtFixedRate(updateTask, 500L, 500L);
        }

        // register screen state listener.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver, filter);
    }

    private void startForeground() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("app_lock", "Protect your apps.", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        PackageManager pm = getPackageManager();
        Intent splash = pm.getLaunchIntentForPackage(getPackageName());

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "app_lock")
                .setContentTitle(getString(R.string.applock_title))
                .setContentText(getString(R.string.applock_content))
                .setTicker(getString(R.string.applock_ticker))
                .setSmallIcon(R.drawable.nonpermitted_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.permitted_icon));

        mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, splash, PendingIntent.FLAG_UPDATE_CURRENT));

        Notification n = mBuilder.build();
        startForeground(NOTIF_ID, n);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public boolean isConcernedAppIsInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String topApp;
        if (Build.VERSION.SDK_INT < 21) {
            topApp = getTopAppPackageNameFromTasks(manager);
        } else {
            topApp = getTopAppPackageNameFromStats(manager);
        }

        boolean needsLock = false;
        if (topApp != null && packageNamesNeedLock != null && packageNamesNeedLock.contains(topApp)) {
            currentApp = topApp;
            needsLock = true;
        }
        if (topApp != null) {
            String FORMAT = "isConcernedAppIsInForeground(%5.5s): TOPAPP: %-25.25s \tCURRENT: %-25.25s";
            Log.v(TAG, String.format(FORMAT, needsLock, topApp, currentApp));
        }
        if (topApp != null && AUTO_CLOSE > 0) {
            if (AUTO_CLOSE < 60000) {
                Log.w(TAG, "Minimum auto close interval should be 60 secs.");
                AUTO_CLOSE = 60000;
            }
            Long time = recentlyUnlockedMap.get(currentApp);
            if (time != null && currentApp.equals(topApp)) {
                long diff = System.currentTimeMillis() - time;
                if (diff > AUTO_CLOSE) {
                    recentlyUnlockedMap.remove(currentApp);
                    return true;
                    //                    AppLockConstants.startHomeLauncher(this);
                }
            }
        }
        return needsLock;
    }

    private String getTopAppPackageNameFromTasks(ActivityManager manager) {
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
        StringBuilder b = new StringBuilder();
        for (ActivityManager.RunningTaskInfo info : task) {
            b.append(info.topActivity.getPackageName()).append("\t\t\t");
        }
        if (task.size() > 0) {
            ComponentName componentInfo = task.get(0).topActivity;
            return componentInfo.getPackageName();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String getTopAppPackageNameFromStats(ActivityManager manager) {
        long time = System.currentTimeMillis();

        @SuppressLint("WrongConstant")
        UsageStatsManager usage = (UsageStatsManager) getSystemService("usagestats");
        List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 2000, time);
        if (stats != null) {
            long last = 0;
            String pack = null;
            for (UsageStats usageStats : stats) {
                String pn = usageStats.getPackageName();
                if (!TextUtils.isEmpty(pn) && usageStats.getLastTimeUsed() > last && !"android".equals(pn)) {
                    last = usageStats.getLastTimeUsed();
                    pack = pn;
                }
            }
            return pack;
        } else {
            try {
                return manager.getRunningAppProcesses().get(0).processName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

}
