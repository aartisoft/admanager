package com.admanager.colorcallscreen.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateService extends Service {
    public static final String TAG = "PhoneStateService";
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    public static void start(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // There is an official solution for devices after M, so we should listen only pre M devices
            context.startService(new Intent(context, PhoneStateService.class));
        }
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, PhoneStateService.class));
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        //        startForeground(123, new Notification());
        this.phoneStateListener = new CustomPhoneStateListener(this);
        this.telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_NONE);
        this.telephonyManager = null;
        this.phoneStateListener = null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }
}