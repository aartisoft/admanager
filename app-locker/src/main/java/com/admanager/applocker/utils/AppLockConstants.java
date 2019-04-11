package com.admanager.applocker.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by amitshekhar on 30/04/15.
 */
public class AppLockConstants {
    public static final String LOCKED = "locked";
    public static final String UNLOCKED = "unlocked";
    public static final String ALL_APPS = "all_apps";

    public static void startHomeLauncher(Context context) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(i);
    }

}
