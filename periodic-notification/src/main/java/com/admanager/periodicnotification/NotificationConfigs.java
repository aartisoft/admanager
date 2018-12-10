package com.admanager.periodicnotification;

import android.app.Activity;
import android.support.annotation.DrawableRes;

public class NotificationConfigs {
    public Class<? extends Activity> clickToGo;

    @DrawableRes
    public int smallIcon;

    @DrawableRes
    public int largeIcon;

    public NotificationConfigs(Class<? extends Activity> clickToGo, @DrawableRes int smallIcon, @DrawableRes int largeIcon) {
        this.clickToGo = clickToGo;
        this.smallIcon = smallIcon;
        this.largeIcon = largeIcon;
    }
}