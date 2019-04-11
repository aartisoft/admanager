package com.admanager.boosternotification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.boosternotification.R;

public class BatteryStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "BatteryStatusReceiver";
    private int level;

    public BatteryStatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive:" + intent.getAction());
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        BoosterNotificationApp.checkAndDisplay(context);
    }

    public void updateUI(RemoteViews contentView, int containerId, int imageId, int textId) {
        contentView.setTextViewText(textId, String.format("%s %%", level));
        if (level < 20) {
            contentView.setImageViewResource(imageId, R.drawable.battery_poor);
        } else {
            contentView.setImageViewResource(imageId, R.drawable.battery_active);
        }
    }
}