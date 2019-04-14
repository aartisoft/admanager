package com.admanager.applocker.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.admanager.applocker.utils.AppLockInitializer;

public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if (action == null) {
            return;
        }

        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_PACKAGE_RESTARTED)) {
            if (AppLockInitializer.isPermissionsGranted(context)) {
                AppCheckServices.startServiceAndSetAlarm(context);
            }
        }
    }
}
