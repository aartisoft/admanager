package com.admanager.colorcallscreen.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import com.admanager.colorcallscreen.activities.CCSInCallActivity;

@TargetApi(Build.VERSION_CODES.M)
public class CallService extends InCallService {

    private final String LOG_TAG = "CallService";
    Call.Callback callCallback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            Log.i(LOG_TAG, "Call.Callback onStateChanged: $call, state: $state");
            CallManagerApi23.getInstance(getApplicationContext()).updateCall(call);
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.i(LOG_TAG, "onCallAdded: $call");
        call.registerCallback(callCallback);
        Intent intent = new Intent(this, CCSInCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        CallManagerApi23.getInstance(getApplicationContext()).updateCall(call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.i(LOG_TAG, "onCallRemoved: $call");
        call.unregisterCallback(callCallback);
        CallManagerApi23.getInstance(getApplicationContext()).updateCall(null);
    }


}
