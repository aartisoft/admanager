package com.admanager.colorcallscreen.service;

import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;

import com.admanager.core.AdmUtils;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class CallManagerCompat {
    private static final String TAG = "CallManagerCompat";
    private static final BehaviorSubject<GsmCall> subject = BehaviorSubject.create();
    private static CallManagerCompat INSTANCE;
    private final WeakReference<Context> context;

    private CallManagerCompat(Context context) {
        this.context = new WeakReference<>(context.getApplicationContext());
    }

    public static CallManagerCompat getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CallManagerCompat(context);
        }
        return INSTANCE;
    }

    final void update(@Nullable GsmCall call) {
        if (call == null) {
            return;
        }
        subject.onNext(call);
    }

    public final Observable<GsmCall> updates() {
        return subject;
    }

    public final void cancelCall() {
        Context context = this.context.get();
        if (AdmUtils.isContextInvalid(context)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CallManagerApi23.getInstance(context).cancel();
        } else {
            CallManager.getInstance(context).cancel();
        }
    }

    public final void acceptCall() {
        Context context = this.context.get();
        if (AdmUtils.isContextInvalid(context)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CallManagerApi23.getInstance(context).accept();
        } else {
            CallManager.getInstance(context).accept();
        }
    }
}
