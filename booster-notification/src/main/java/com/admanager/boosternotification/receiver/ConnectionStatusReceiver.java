package com.admanager.boosternotification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.boosternotification.R;

import java.lang.reflect.Method;

public class ConnectionStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "DataStatusReceiver";
    private boolean dataEnabled;

    public ConnectionStatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive:" + intent.getAction());

        BoosterNotificationApp.checkAndDisplay(context);
    }

    public void updateDataUI(Context context, RemoteViews contentView, int containerId, int imageId, int textId) {
        if (isDataEnabled(context)) {
            contentView.setImageViewResource(imageId, R.drawable.data_active);
        } else {
            contentView.setImageViewResource(imageId, R.drawable.data_passive);
        }
    }

    public void updateWifiUI(Context context, RemoteViews contentView, int containerId, int imageId, int textId) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        int wifiState = wifiManager.getWifiState();
        boolean wifiEnabled = wifiManager.isWifiEnabled();

        Log.e(TAG, "updateUI:" + wifiState);
        switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                updateImage(contentView, imageId, R.drawable.wifi_passive, 100);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                updateImage(contentView, imageId, R.drawable.wifi_active, 70);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                updateImage(contentView, imageId, R.drawable.wifi_active, 100);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                updateImage(contentView, imageId, R.drawable.wifi_active, 30);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                updateImage(contentView, imageId, R.drawable.wifi_passive, 10);
                break;
        }
    }

    private void updateImage(RemoteViews contentView, int imageId, int wifi_passive, int percentage) {
        contentView.setImageViewResource(imageId, wifi_passive);
        contentView.setInt(imageId, "setAlpha", (percentage * 255) / 100);
    }

    public boolean isDataEnabled(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            return (Boolean) method.invoke(cm);
        } catch (Exception e) {
            try {
                NetworkInfo activeNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return activeNetwork != null && activeNetwork.isConnected();
            } catch (Exception ignored) {

            }
        }
        return false;
    }

}