package com.admanager.boosternotification.receiver;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.admanager.boosternotification.BoosterNotificationApp;

public class BoosterNotificationReceiver extends BroadcastReceiver {
    public static final String TAG = "Booster";
    public static final String PARAM_COLLAPSE = "auto_collapse";
    public static final String ACTION_BOOST = "action_boost";
    public static final String ACTION_CPU = "action_cpu";
    public static final String ACTION_BATTERY = "action_battery";
    public static final String ACTION_DATA = "action_data";
    public static final String ACTION_FLASHLIGHT = "action_flashlight";
    public static final String ACTION_WIFI = "action_wifi";
    private static Camera cam;

    public static boolean isFlashlightOn() {
        if (cam != null) {
            Camera.Parameters p = cam.getParameters();
            String flashMode = p.getFlashMode();
            return flashMode != null && flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH);
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if (action == null) {
            return;
        }

        // close status bar
        boolean collapse = intent.getBooleanExtra(PARAM_COLLAPSE, false);
        if (collapse) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }

        switch (action) {
            case ACTION_BOOST:
                boost(collapse);
                break;
            case ACTION_CPU:
                cpu(collapse);
                break;
            case ACTION_BATTERY:
                battery(collapse);
                break;
            case ACTION_DATA:
                data(context, collapse);
                break;
            case ACTION_FLASHLIGHT:
                flashlight(collapse);
                break;
            case ACTION_WIFI:
                wifi(context, collapse);
                break;
        }

        BoosterNotificationApp.checkAndDisplay(context);
    }

    private void boost(boolean collapse) {

    }

    private void cpu(boolean collapse) {

    }

    private void battery(boolean collapse) {

    }

    private void data(Context context, boolean collapse) {
        String SETTINGS_PACKAGE = "com.android.settings";
        String SETTINGS_CLASS_DATA_USAGE_SETTINGS = "com.android.settings.Settings$DataUsageSummaryActivity";

        try {
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            final ComponentName cn = new ComponentName(SETTINGS_PACKAGE, SETTINGS_CLASS_DATA_USAGE_SETTINGS);
            intent.setComponent(cn);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.v(TAG, "Data settings usage Activity is not present");
        }
    }

    private void flashlight(boolean collapse) {
        if (cam == null) {
            cam = Camera.open();
        }
        if (isFlashlightOn()) {
            cam.stopPreview();
            cam.release();
            cam = null;
            return;
        }
        Camera.Parameters p = cam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
    }

    private void wifi(Context context, boolean collapse) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        wifiManager.setWifiEnabled(!wifiEnabled);
    }


}
