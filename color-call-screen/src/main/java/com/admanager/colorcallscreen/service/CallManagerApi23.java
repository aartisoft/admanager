package com.admanager.colorcallscreen.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.telecom.Call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.admanager.colorcallscreen.model.ContactBean;

@TargetApi(Build.VERSION_CODES.M)
final class CallManagerApi23 extends BaseCallManager {
    private static CallManagerApi23 INSTANCE;
    private static Call currentCall;

    private CallManagerApi23(Context context) {
        super(context);
    }

    static CallManagerApi23 getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CallManagerApi23(context);
        }
        return INSTANCE;
    }

    void updateCall(@Nullable Call call) {
        currentCall = call;
        if (call == null) {
            return;
        }
        GsmCall gsmCall = toGsmCall(call);
        CallManagerCompat.getInstance(context.get()).update(gsmCall);
    }

    public void cancel() {
        if (currentCall == null) {
            return;
        }
        switch (currentCall.getState()) {
            case Call.STATE_RINGING:
                rejectCall();
                break;
            default:
                disconnectCall();
        }
    }

    public void accept() {
        if (currentCall == null) {
            return;
        }
        Call.Details details = currentCall.getDetails();
        currentCall.answer(details.getVideoState());

    }

    private GsmCall toGsmCall(@NonNull Call call) {
        GsmCall.Status status = toGsmCallStatus(call.getState());
        Call.Details details = call.getDetails();
        Uri uri = details.getHandle();
        if (uri == null) {
            return new GsmCall(status, new ContactBean(null, "", "", null));
        }
        String str = uri.getSchemeSpecificPart();
        ContactBean a = Utils.readContact(context.get(), str);
        return new GsmCall(status, a);
    }

    private GsmCall.Status toGsmCallStatus(int state) {
        switch (state) {
            case Call.STATE_DIALING:
                return GsmCall.Status.DIALING;
            case Call.STATE_RINGING:
                return GsmCall.Status.RINGING;
            case Call.STATE_ACTIVE:
                return GsmCall.Status.ACTIVE;
            case Call.STATE_DISCONNECTED:
                return GsmCall.Status.DISCONNECTED;
            case Call.STATE_CONNECTING:
                return GsmCall.Status.CONNECTING;
            case Call.STATE_HOLDING:
                return GsmCall.Status.HOLDING;
            default:
                return GsmCall.Status.UNKNOWN;
        }
    }

    private void rejectCall() {
        if (currentCall == null) {
            return;
        }
        currentCall.reject(false, "");
    }

    private void disconnectCall() {
        if (currentCall == null) {
            return;
        }
        currentCall.disconnect();
    }
}
