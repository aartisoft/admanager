package com.admanager.boosternotification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if (action == null) {
            return;
        }

        boolean collapse = intent.getBooleanExtra(PARAM_COLLAPSE, false);

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
                data(collapse);
                break;
            case ACTION_FLASHLIGHT:
                flashlight(collapse);
                break;
            case ACTION_WIFI:
                wifi(collapse);
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

    private void data(boolean collapse) {

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

    public static boolean isFlashlightOn() {
        if (cam != null) {
            Camera.Parameters p = cam.getParameters();
            String flashMode = p.getFlashMode();
            return flashMode != null && flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH);
        }
        return false;
    }

    private void wifi(boolean collapse) {

    }


}
