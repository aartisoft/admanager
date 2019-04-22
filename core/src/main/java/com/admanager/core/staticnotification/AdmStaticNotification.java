package com.admanager.core.staticnotification;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.admanager.core.AdmUtils;
import com.admanager.core.BaseHelper;

import java.lang.ref.WeakReference;

public class AdmStaticNotification extends BaseHelper {
    private static final String TAG = "AdmStaticNotification";
    private static final int ID = 52189;
    private static final String PREF_FILE_NAME = "notif_helper_prefs";
    private static final String EASY_ACCESS = "easy_access";
    private static AdmStaticNotification INSTANCE;
    private String title;
    private String text;
    private String channelId;
    private String channelName;
    private int iconLarge;
    private int iconSmall;
    private Intent intent;
    private Class<? extends Activity> startIn;

    private AdmStaticNotification(Application application, Class<? extends Activity> startIn, String title, String text, String channelId, String channelName, int iconLarge, int iconSmall, Intent intent) {
        super(application);
        this.title = title;
        this.text = text;
        this.channelId = channelId;
        this.channelName = channelName;
        this.iconLarge = iconLarge;
        this.iconSmall = iconSmall;
        this.intent = intent;
        this.startIn = startIn;
    }

    private static AdmStaticNotification getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("You should initialize AdmStaticNotification!");
        }
        return INSTANCE;
    }

    private static AdmStaticNotification init(AdmStaticNotification admStaticNotification) {
        INSTANCE = admStaticNotification;
        return INSTANCE;
    }

    private static void checkAndDisplay(Context context) {
        if (AdmUtils.isContextInvalid(context)) {
            return;
        }
        final SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        boolean isChecked = getStatus(sharedPreferences);

        if (isChecked) {
            AdmStaticNotification i = AdmStaticNotification.getInstance();
            show(context, i.title, i.text, i.channelId, i.channelName, i.iconLarge, i.iconSmall, i.intent);
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
            throw new IllegalStateException("Given menuId couldn't found!");
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

    private static void show(Context context, String title, String content, String channelID, String channelName, int largeIcon, int smallIcon, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setOngoing(true);
        nm.notify(ID, builder.build());
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

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (AdmUtils.isActivityEquals(activity, startIn)) {
            checkAndDisplay(activity);
        }
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private String text;
        private String channelId = "easy_access";
        private String channelName = "Easy Access";
        private int iconLarge;
        private int iconSmall;
        private Intent intent;
        private Class<? extends Activity> startIn;

        public Builder(@NonNull Application context, String title, String text) {
            this.context = new WeakReference<>(context.getApplicationContext());
            this.title = title;
            this.text = text;
        }

        public Builder(@NonNull Application context, @StringRes int title, @StringRes int text) {
            this(context, context.getString(title), context.getString(text));
        }

        public Builder channelId(String channelId) {
            if (channelId == null) {
                throw new IllegalArgumentException("null channelId is not allowed!");
            }
            this.channelId = channelId;
            return this;
        }

        public Builder channelId(@StringRes int channelId) {
            Context c = context.get();
            if (c != null) {
                this.channelId = c.getString(channelId);
            }
            return this;
        }

        public Builder channelName(String channelName) {
            if (channelName == null) {
                throw new IllegalArgumentException("null channelName is not allowed!");
            }
            this.channelName = channelName;
            return this;
        }

        public Builder channelName(@StringRes int channelName) {
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
        public Builder iconBig(@DrawableRes int iconLarge) {
            return iconLarge(iconLarge);
        }

        public Builder iconLarge(@DrawableRes int iconLarge) {
            this.iconLarge = iconLarge;
            return this;
        }

        public Builder iconSmall(@DrawableRes int iconSmall) {
            this.iconSmall = iconSmall;
            return this;
        }

        public Builder onClick(Class<? extends Activity> activity) {
            Context c = context.get();
            if (c != null) {
                this.intent = new Intent(c, activity);
            }
            return this;
        }

        public Builder onClick(Intent intent) {
            this.intent = intent;
            return this;
        }

        public Builder startInActivity(Class<? extends Activity> startIn) {
            this.startIn = startIn;
            return this;
        }

        public void build() {
            Context context = this.context.get();
            setDefaultIcons(context);
            setDefaultIntent(context);
            setDefaultStartIn(context);
            Application app = (Application) context.getApplicationContext();
            AdmStaticNotification.init(new AdmStaticNotification(app, startIn, title, text, channelId, channelName, iconLarge, iconSmall, intent));
            AdmStaticNotification.checkAndDisplay(context);
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
