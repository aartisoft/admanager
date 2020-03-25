package com.admanager.boosternotification;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RemoteViews;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;

import com.admanager.boosternotification.receiver.BatteryStatusReceiver;
import com.admanager.boosternotification.receiver.BoosterNotificationReceiver;
import com.admanager.boosternotification.receiver.ConnectionStatusReceiver;
import com.admanager.core.AdmUtils;
import com.admanager.core.Ads;
import com.admanager.core.AdsImp;
import com.admanager.core.BaseHelper;
import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;

public class BoosterNotificationApp extends BaseHelper {
    private static final String TAG = "BoosterNotificationApp";
    private static final int ID = 52188;
    private static final String PREF_FILE_NAME = "notif_helper_prefs";
    private static final String EASY_ACCESS = "easy_access";
    private static com.admanager.boosternotification.BoosterNotificationApp INSTANCE;
    private static BatteryStatusReceiver batteryStatusReceiver;
    private static ConnectionStatusReceiver connectionStatusReceiver;
    private String channelId;
    private String channelName;
    private int iconLarge;
    private int appLauncherIcon;
    private boolean removeAppLauncherIconBg;
    private Ads ads;
    private int iconSmall;
    private Intent intent;
    private Class<? extends Activity> startIn;

    private BoosterNotificationApp(Application application, Ads ads, Class<? extends Activity> startIn, String channelId, String channelName, int iconLarge, int iconSmall, int appLauncherIcon, boolean removeAppLauncherIconBg, Intent intent) {
        super(application);
        this.ads = ads;
        this.channelId = channelId;
        this.channelName = channelName;
        this.iconLarge = iconLarge;
        this.appLauncherIcon = appLauncherIcon;
        this.removeAppLauncherIconBg = removeAppLauncherIconBg;
        this.iconSmall = iconSmall;
        this.intent = intent;
        this.startIn = startIn;
    }

    public static com.admanager.boosternotification.BoosterNotificationApp getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("You should initialize BoosterNotificationApp!");
        }
        return INSTANCE;
    }

    private static com.admanager.boosternotification.BoosterNotificationApp init(com.admanager.boosternotification.BoosterNotificationApp BoosterNotificationApp) {
        INSTANCE = BoosterNotificationApp;
        return INSTANCE;
    }

    public static void checkAndDisplay(Context context) {
        if (AdmUtils.isContextInvalid(context)) {
            return;
        }
        final SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        boolean isChecked = getStatus(sharedPreferences);

        if (isChecked) {
            com.admanager.boosternotification.BoosterNotificationApp i = com.admanager.boosternotification.BoosterNotificationApp.getInstance();
            show(context, i.channelId, i.channelName, i.iconLarge, i.iconSmall, i.intent, i.appLauncherIcon, i.removeAppLauncherIconBg);
        } else {
            hide(context);
        }
    }

    public static void saveStatus(Context context, boolean show) {
        if (AdmUtils.isContextInvalid(context)) {
            return;
        }
        final SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(EASY_ACCESS, show).apply();

        checkAndDisplay(context);
    }

    public static void configureSwitchMenu(@NonNull final NavigationView navigationView, @IdRes int menuId) {
        if (navigationView == null) {
            return;
        }
        final Context context = navigationView.getContext();
        final SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

        MenuItem item = navigationView.getMenu().findItem(menuId);
        if (item == null) {
            throw new IllegalStateException("Given menuId couldn't found !");
        }
        View actionView = item.getActionView();
        if (!(actionView instanceof SwitchCompat)) {
            throw new IllegalStateException("Given menuId should be SwitchCompat! You can add a menu item like below:" +
                    "<item\n" +
                    "            android:id=\"@+id/nav_notification\"\n" +
                    "            android:title=\"@string/nav_notification\"\n" +
                    "            app:actionLayout=\"@layout/menu_switch\"\n" +
                    "            app:showAsAction=\"always\" />");
        }
        SwitchCompat switchCompat = (SwitchCompat) actionView;

        switchCompat.setChecked(getStatus(sharedPreferences));
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v(TAG, "onCheckedChanged" + isChecked);
                sharedPreferences.edit().putBoolean(EASY_ACCESS, isChecked).apply();

                checkAndDisplay(context);
            }
        });
    }

    private static void show(Context context, String channelID, String channelName, int largeIcon, int smallIcon, Intent intent, int appLauncherIcon, boolean removeAppLauncherIconBg) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
        }

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.adm_booster_notification);
        contentView.setOnClickPendingIntent(R.id.p1, getPendingIntent(context, BoosterNotificationReceiver.ACTION_BOOST, true));
        contentView.setOnClickPendingIntent(R.id.p2, getPendingIntent(context, BoosterNotificationReceiver.ACTION_LAUNCH, true));
        contentView.setOnClickPendingIntent(R.id.p3, getPendingIntent(context, BoosterNotificationReceiver.ACTION_BATTERY, true));
        contentView.setOnClickPendingIntent(R.id.p4, getPendingIntent(context, BoosterNotificationReceiver.ACTION_DATA, true));
        contentView.setOnClickPendingIntent(R.id.p5, getPendingIntent(context, BoosterNotificationReceiver.ACTION_FLASHLIGHT, false));
        contentView.setOnClickPendingIntent(R.id.p6, getPendingIntent(context, BoosterNotificationReceiver.ACTION_WIFI, false));

        boolean hasCameraFlash = false;
        try {
            hasCameraFlash = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (hasCameraFlash) {
            contentView.setViewVisibility(R.id.p5, View.VISIBLE);
            contentView.setImageViewResource(R.id.i5, BoosterNotificationReceiver.isFlashOpen ? R.drawable.torch_active
                    : R.drawable.torch_passive);
        } else {
            contentView.setViewVisibility(R.id.p5, View.GONE);
        }

        if (batteryStatusReceiver != null) {
            batteryStatusReceiver.updateUI(contentView, R.id.p3, R.id.i3, R.id.t3);
        }

        if (connectionStatusReceiver != null) {
            connectionStatusReceiver.updateWifiUI(context, contentView, R.id.p6, R.id.i6, R.id.t6);
            connectionStatusReceiver.updateDataUI(context, contentView, R.id.p4, R.id.i4, R.id.t4);
        }

        if (appLauncherIcon != 0) {
            contentView.setImageViewResource(R.id.i2, appLauncherIcon);

            if (removeAppLauncherIconBg) {
                contentView.setInt(R.id.i2, "setBackgroundResource", 0);
                contentView.setViewPadding(R.id.i2, 0, 0, 0, 0);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                .setSmallIcon(smallIcon)
                .setContent(contentView);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setOngoing(true);
        nm.notify(ID, builder.build());
    }

    private static PendingIntent getPendingIntent(Context context, String action, boolean collapse) {
        int currentTimeMillis = (int) System.currentTimeMillis();
        Intent intent = new Intent(context, BoosterNotificationReceiver.class);
        intent.putExtra(BoosterNotificationReceiver.PARAM_COLLAPSE, collapse);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, currentTimeMillis, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static void hide(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ID);
    }

    public static boolean getStatus(Context context) {
        if (AdmUtils.isContextInvalid(context)) {
            return false;
        }
        final SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return getStatus(sharedPreferences);
    }

    private static boolean getStatus(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(EASY_ACCESS, true);
    }

    public Ads getAds() {
        return ads;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (AdmUtils.isActivityEquals(activity, startIn)) {
            checkAndDisplay(activity);
        }
    }


    public static class Builder {

        private final WeakReference<Context> context;
        private String channelId = "easy_access";
        private String channelName = "Easy Access";
        private int iconLarge;
        private int iconSmall;
        private Intent intent;
        private Class<? extends Activity> startIn;
        private Ads ads;
        private int appLauncherIcon;
        private boolean removeAppLauncherIconBg;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public BoosterNotificationApp.Builder appLauncherIcon(@DrawableRes int appLauncherIcon) {
            return appLauncherIcon(appLauncherIcon, false);
        }

        public BoosterNotificationApp.Builder appLauncherIcon(@DrawableRes int appLauncherIcon, boolean removeDefaultBg) {
            this.appLauncherIcon = appLauncherIcon;
            this.removeAppLauncherIconBg = removeDefaultBg;
            return this;
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public BoosterNotificationApp.Builder channelId(String channelId) {
            if (channelId == null) {
                throw new IllegalArgumentException("null channelId is not allowed!");
            }
            this.channelId = channelId;
            return this;
        }

        public BoosterNotificationApp.Builder channelId(@StringRes int channelId) {
            Context c = context.get();
            if (c != null) {
                this.channelId = c.getString(channelId);
            }
            return this;
        }

        public BoosterNotificationApp.Builder channelName(String channelName) {
            if (channelName == null) {
                throw new IllegalArgumentException("null channelName is not allowed!");
            }
            this.channelName = channelName;
            return this;
        }

        public BoosterNotificationApp.Builder channelName(@StringRes int channelName) {
            Context c = context.get();
            if (c != null) {
                this.channelName = c.getString(channelName);
            }
            return this;
        }

        /**
         * Use {@link #iconLarge(int) }
         */
        @Deprecated
        public BoosterNotificationApp.Builder iconBig(@DrawableRes int iconLarge) {
            return iconLarge(iconLarge);
        }

        public BoosterNotificationApp.Builder iconLarge(@DrawableRes int iconLarge) {
            this.iconLarge = iconLarge;
            return this;
        }

        public BoosterNotificationApp.Builder iconSmall(@DrawableRes int smallIcon) {
            this.iconSmall = smallIcon;
            return this;
        }

        public BoosterNotificationApp.Builder onClick(Class<? extends Activity> activity) {
            Context c = context.get();
            if (c != null) {
                this.intent = new Intent(c, activity);
            }
            return this;
        }

        public BoosterNotificationApp.Builder onClick(Intent intent) {
            this.intent = intent;
            return this;
        }

        public BoosterNotificationApp.Builder startInActivity(Class<? extends Activity> startIn) {
            this.startIn = startIn;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();
            setDefaultIcons(context);
            setDefaultIntent(context);
            setDefaultStartIn(context);

            Application app = (Application) context.getApplicationContext();
            BoosterNotificationApp.init(new BoosterNotificationApp(app, ads, startIn, channelId, channelName, iconLarge, iconSmall, appLauncherIcon, removeAppLauncherIconBg, intent));
            BoosterNotificationApp.checkAndDisplay(context);

            batteryStatusReceiver = new BatteryStatusReceiver();
            context.registerReceiver(batteryStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            connectionStatusReceiver = new ConnectionStatusReceiver();
            context.registerReceiver(connectionStatusReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            context.registerReceiver(connectionStatusReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
            context.registerReceiver(connectionStatusReceiver, new IntentFilter("android.net.ConnectivityManager.CONNECTIVITY_ACTION"));

        }

        private void setDefaultStartIn(Context context) {
            if (this.startIn == null) {
                try {
                    PackageManager pm = context.getPackageManager();
                    Intent launchIntentForPackage = pm.getLaunchIntentForPackage(context.getPackageName());
                    this.startIn = (Class<? extends Activity>) Class.forName(launchIntentForPackage.getComponent().getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        private void setDefaultIntent(Context context) {
            if (this.intent == null) {
                PackageManager pm = context.getPackageManager();
                this.intent = pm.getLaunchIntentForPackage(context.getPackageName());
            }
        }

        private void setDefaultIcons(Context context) {
            if (this.iconLarge == 0 || this.iconSmall == 0) {
                try {
                    ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                    int icon = info.icon;
                    if (this.iconLarge == 0) {
                        this.iconLarge = icon;
                    }
                    if (this.iconSmall == 0) {
                        this.iconSmall = icon;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (this.iconLarge == 0) {
                this.iconLarge = android.R.drawable.ic_popup_reminder;
            }
            if (this.iconSmall == 0) {
                this.iconSmall = android.R.drawable.sym_action_chat;
            }

        }
    }
}
