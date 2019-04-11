package com.admanager.boosternotification.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import static com.admanager.boosternotification.BoosterNotificationApp.checkAndDisplay;

public class BatteryStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "BatteryStatusReceiver";
    private BatteryStatus batteryStatus;

    public BatteryStatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        float temp = (float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10;
        float voltage = (float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000;

        Log.d(TAG, this + ":" + intent.getExtras());

        updateBatteryStatus(context, level, temp, voltage);
    }

    public void updateBatteryStatus(Context context, int level, float temp, float voltage) {
        if (batteryStatus == null) {
            batteryStatus = new BatteryStatus();
        }

        batteryStatus.setLevel(level);
        batteryStatus.setTemp(temp);
        batteryStatus.setVoltage(voltage);
        checkAndDisplay(context);
    }

    public boolean hasRecievedStatus() {
        return batteryStatus != null;
    }

    public CharSequence getStatusString() {
        if (batteryStatus == null) return "";
        return batteryStatus.getStatusString();
    }
}