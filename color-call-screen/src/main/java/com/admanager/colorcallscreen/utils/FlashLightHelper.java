package com.admanager.colorcallscreen.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class FlashLightHelper {
    public static final String TAG = "FlashLightHelper";

    private static long BLINK_DELAY = 150;
    private static long TIMEOUT = 90_000L;

    private static FlashLightHelper INSTANCE;
    private CameraManager camManager;
    private Camera cam;
    private AsyncTask<Void, Void, Void> TASK;
    private boolean flashEnabled;

    private FlashLightHelper(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
        flashEnabled = Prefs.with(context).isFlashEnabled();
    }

    public static FlashLightHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FlashLightHelper(context);
        }
        INSTANCE.flashEnabled = Prefs.with(context).isFlashEnabled();
        return INSTANCE;
    }

    @SuppressLint("StaticFieldLeak")
    public void start() {
        if (!flashEnabled) {
            return;
        }

        TASK = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                boolean state = false;
                long time = System.currentTimeMillis();
                while (!isCancelled()) {
                    if (System.currentTimeMillis() - time > TIMEOUT) {
                        cancel(true);
                        break;
                    }

                    state = !state;
                    flashlight(state);
                    try {
                        Thread.sleep(BLINK_DELAY);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                flashlight(false);
                return null;
            }
        };
        TASK.execute();
    }

    public void stop() {
        if (TASK != null) {
            TASK.cancel(true);
            TASK = null;
        }
    }

    private void flashlight(boolean open) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (camManager != null) {
                    String cameraId = camManager.getCameraIdList()[0];
                    camManager.setTorchMode(cameraId, open);
                }
            } else {
                if (cam == null) {
                    cam = Camera.open();
                }
                if (!open) {
                    cam.stopPreview();
                    cam.release();
                    cam = null;
                    return;
                }
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
            }
        } catch (Throwable e) {
            Log.e(TAG, "Flash problem: " + e.getMessage());
        }
    }
}
