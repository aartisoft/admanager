package com.admanager.colorcallscreen.service;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.util.Log;

import com.admanager.colorcallscreen.model.ContactBean;

public class CustomPhoneStateListener extends PhoneStateListener {
    public static final String TAG = "PhoneStateLog";
    private Context context;

    public CustomPhoneStateListener(Context context) {
        Log.d(TAG, "CustomPhoneStateListener");
        this.context = context;
    }

    @Override
    public final void onCallStateChanged(int i, String str) {
        super.onCallStateChanged(i, str);
        Log.d(TAG, "onCallStateChanged i:" + i + " str:" + str);

        ContactBean a = Utils.readContact(this.context, str);
        CallManager.getInstance(context).updateCall(a, i);
    }

}
