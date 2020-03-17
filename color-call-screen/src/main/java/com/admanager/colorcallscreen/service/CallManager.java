package com.admanager.colorcallscreen.service;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.admanager.colorcallscreen.activities.CCSInCallActivity;
import com.admanager.colorcallscreen.model.ContactBean;
import com.admanager.core.AdmUtils;
import com.android.internal.telephony.ITelephony;

import java.io.IOException;
import java.lang.reflect.Method;

final class CallManager extends BaseCallManager {
    private static final String MANUFACTURER_HTC = "HTC";
    private static CallManager INSTANCE;
    private static ContactBean contactBean;
    private TelephonyManager tm;
    private KeyguardManager keyguardManager;
    private AudioManager audioManager;

    private CallManager(Context context) {
        super(context);
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    static CallManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CallManager(context);
        }
        return INSTANCE;
    }

    void updateCall(@Nullable final ContactBean contactBean, int state) {
        CallManager.contactBean = contactBean;
        GsmCall.Status st;

        if (contactBean == null) {
            st = GsmCall.Status.UNKNOWN;
        } else {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    st = GsmCall.Status.DISCONNECTED;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    st = GsmCall.Status.ACTIVE;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    st = GsmCall.Status.RINGING;
                    break;
                default:
                    st = GsmCall.Status.UNKNOWN;
            }

        }

        startAct();
        GsmCall call = new GsmCall(st, contactBean);
        CallManagerCompat.getInstance(context.get()).update(call);
    }

    private void startAct() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Context context = CallManager.this.context.get();
                if (AdmUtils.isContextInvalid(context)) {
                    return;
                }
                Intent myIntent = new Intent(context, CCSInCallActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(myIntent);
            }
        }, 900);
    }

    public void cancel() {
        endCall();
    }

    public void accept() {
        if (contactBean == null) {
            return;
        }
        acceptCall();
        //        tm.acceptRingingCall();
        //        Call.Details details = contactBean.getDetails();
        //        contactBean.answer(details.getVideoState());

    }

    private void endCall() {
        try {
            ITelephony telephonyService = getTelephonyService();
            telephonyService.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acceptCall() {
        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER) && !audioManager.isWiredHeadsetOn();

        if (broadcastConnected) {
            broadcastHeadsetConnected(false);
        }

        try {

            try {
                // logger.debug("execute input keycode headset hook");
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));

            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                //    logger.debug("send keycode headset hook intents");
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";

                Context c = this.context.get();
                c.sendOrderedBroadcast(headsetButtonIntent(KeyEvent.ACTION_DOWN), enforcedPerm);
                c.sendOrderedBroadcast(headsetButtonIntent(KeyEvent.ACTION_UP), enforcedPerm);
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(false);
            }
        }
    }

    private Intent headsetButtonIntent(int action) {
        return new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                Intent.EXTRA_KEY_EVENT, new KeyEvent(action, KeyEvent.KEYCODE_HEADSETHOOK));
    }

    private void broadcastHeadsetConnected(boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            context.get().sendOrderedBroadcast(i, null);
        } catch (Exception e) {
        }
    }

    private ITelephony getTelephonyService() throws Exception {
        Class c = Class.forName(tm.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        return (ITelephony) m.invoke(tm);
    }

}
