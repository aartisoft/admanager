package com.admanager.boosternotification.receiver;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.boosternotification.boost.BatteryBoostActivity;
import com.admanager.boosternotification.boost.RamBoostActivity;
import com.admanager.core.AdmUtils;
import com.admanager.core.Consts;

public class BoosterNotificationReceiver extends BroadcastReceiver {
    public static final String TAG = "Booster";
    public static final String PARAM_COLLAPSE = "auto_collapse";
    public static final String ACTION_BOOST = "action_boost";
    public static final String ACTION_LAUNCH = "action_launch";
    public static final String ACTION_BATTERY = "action_battery";
    public static final String ACTION_DATA = "action_data";
    public static final String ACTION_FLASHLIGHT = "action_flashlight";
    public static final String ACTION_WIFI = "action_wifi";
    private static Camera cam;
    public static boolean isFlashOpen = false;

    private boolean isFlashlightOn() {
        if (cam != null) {
            Camera.Parameters p = cam.getParameters();
            String flashMode = p.getFlashMode();
            return flashMode != null && flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH);
        }
        return isFlashOpen;
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

        if (AdmUtils.isContextInvalid(context)) {
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
                boost(context);
                break;
            case ACTION_LAUNCH:
                launch(context);
                break;
            case ACTION_BATTERY:
                battery(context);
                break;
            case ACTION_DATA:
                data(context);
                break;
            case ACTION_FLASHLIGHT:
                flashlight(context);
                break;
            case ACTION_WIFI:
                wifi(context);
                break;
        }

        BoosterNotificationApp.checkAndDisplay(context);
    }

    private void boost(Context context) {
        Intent ramBoostIntent = new Intent(context, RamBoostActivity.class);
        ramBoostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(ramBoostIntent);
    }

    private void launch(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent launchIntentForPackage = pm.getLaunchIntentForPackage(context.getPackageName());
        launchIntentForPackage.putExtra(Consts.IntentClickParam.BOOSTER_NOTIFICATION_CLICKED, true);
        launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntentForPackage);
    }

    private void battery(Context context) {
        Intent ramBoostIntent = new Intent(context, BatteryBoostActivity.class);
        ramBoostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(ramBoostIntent);
    }

    private void data(Context context) {
        String SETTINGS_PACKAGE = "com.android.settings";
        String SETTINGS_CLASS_DATA_USAGE_SETTINGS = "com.android.settings.Settings$DataUsageSummaryActivity";

        try {
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            final ComponentName cn = new ComponentName(SETTINGS_PACKAGE, SETTINGS_CLASS_DATA_USAGE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.v(TAG, "Data settings usage Activity is not present");
        }
    }

    private void flashlight(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                if (camManager != null) {
                    String cameraId = camManager.getCameraIdList()[0];
                    camManager.setTorchMode(cameraId, !isFlashOpen);
                    isFlashOpen = !isFlashOpen;
                }
            } else {
                if (cam == null) {
                    cam = Camera.open();
                }
                if (isFlashlightOn()) {
                    cam.stopPreview();
                    cam.release();
                    cam = null;
                    isFlashOpen = false;
                    return;
                }
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                isFlashOpen = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return;
        }
        wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
    }


}
