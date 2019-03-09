package com.admanager.periodicnotification;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.lang.ref.WeakReference;

public class PeriodicNotificationApp {
    private static final String TAG = "PeriodicNotif";
    private static PeriodicNotificationApp INSTANCE;
    private int iconBig;
    private int iconSmall;
    private String channelId;
    private String channelName;
    private PeriodicNotificationKeys keys;
    private Intent intent;

    private PeriodicNotificationApp(PeriodicNotificationKeys keys, int iconBig, int iconSmall, Intent intent, String channelId, String channelName) {
        this.iconBig = iconBig;
        this.iconSmall = iconSmall;
        this.intent = intent;
        this.keys = keys;
        this.channelName = channelName;
        this.channelId = channelId;
    }

    static PeriodicNotificationApp getInstance() {
        //        if (INSTANCE == null) {
        //            throw new IllegalStateException("You should initialize PeriodicNotificationApp!");
        //        }
        return INSTANCE;
    }

    private static PeriodicNotificationApp init(PeriodicNotificationApp admStaticNotification) {
        INSTANCE = admStaticNotification;
        return INSTANCE;
    }

    int getIconBig() {
        return iconBig;
    }

    int getIconSmall() {
        return iconSmall;
    }

    String getChannelId() {
        return channelId;
    }

    String getChannelName() {
        return channelName;
    }

    PeriodicNotificationKeys getKeys() {
        return keys;
    }

    Intent getIntent() {
        return intent;
    }

    public static class Builder {

        private final WeakReference<Application> application;
        private int iconBig;
        private int iconSmall;
        private String channelId = "remind_me";
        private String channelName = "Keeps you updated.";
        private Intent intent;
        private PeriodicNotificationKeys keys;

        public Builder(@NonNull Application application) {
            this.application = new WeakReference<>(application);
        }

        public Builder withRemoteConfigKeys(@NonNull PeriodicNotificationKeys keys) {
            keys.validate();
            this.keys = keys;
            return this;
        }

        public Builder channelId(String channelId) {
            if (channelId == null) {
                throw new IllegalArgumentException("null channelId is not allowed!");
            }
            this.channelId = channelId;
            return this;
        }

        public Builder channelId(@StringRes int channelId) {
            Context c = application.get();
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
            Context c = application.get();
            if (c != null) {
                this.channelName = c.getString(channelName);
            }
            return this;
        }

        public Builder iconBig(@DrawableRes int bigIcon) {
            this.iconBig = bigIcon;
            return this;
        }

        public Builder iconSmall(@DrawableRes int smallIcon) {
            this.iconSmall = smallIcon;
            return this;
        }

        public Builder onClick(Class<? extends Activity> activity) {
            Context c = application.get();
            if (c != null) {
                this.intent = new Intent(c, activity);
            }
            return this;
        }

        public Builder onClick(Intent intent) {
            this.intent = intent;
            return this;
        }

        public void build() {
            Context context = this.application.get();
            setDefaultIcons(context);
            setDefaultIntent(context);
            if (keys == null) {
                keys = new PeriodicNotificationKeys();
            }
            keys.setDefaultValues(context);

            PeriodicNotificationApp.init(new PeriodicNotificationApp(keys, iconBig, iconSmall, intent, channelId, channelName));
        }

        private void setDefaultIntent(Context context) {
            if (this.intent == null) {
                PackageManager pm = context.getPackageManager();
                this.intent = pm.getLaunchIntentForPackage(context.getPackageName());
            }
        }

        private void setDefaultIcons(Context context) {
            if (this.iconBig == 0 || this.iconSmall == 0) {
                try {
                    ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                    int icon = info.icon;
                    if (this.iconBig == 0) {
                        this.iconBig = icon;
                    }
                    if (this.iconSmall == 0) {
                        this.iconSmall = icon;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (this.iconBig == 0) {
                this.iconBig = android.R.drawable.ic_popup_reminder;
            }
            if (this.iconSmall == 0) {
                this.iconSmall = android.R.drawable.sym_action_chat;
            }

        }
    }
}