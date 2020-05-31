package com.admanager.adjust;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.admanager.core.AdmUtils;

public class AdmAdjust {
    public static void init(Application app,
                            @StringRes int _ADJUST_APP_TOKEN,
                            @StringRes int _ADJUST_SECRET_ID,
                            @StringRes int _ADJUST_INFO_1,
                            @StringRes int _ADJUST_INFO_2,
                            @StringRes int _ADJUST_INFO_3,
                            @StringRes int _ADJUST_INFO_4,
                            String pushToken, OnAttributionChangedListener attributionChangedListener) {
        if (AdmUtils.isContextInvalid(app)) {
            return;
        }
        String ADJUST_APP_TOKEN = app.getString(_ADJUST_APP_TOKEN);
        long ADJUST_SECRET_ID = Long.parseLong(app.getString(_ADJUST_SECRET_ID));
        long ADJUST_INFO_1 = Long.parseLong(app.getString(_ADJUST_INFO_1));
        long ADJUST_INFO_2 = Long.parseLong(app.getString(_ADJUST_INFO_2));
        long ADJUST_INFO_3 = Long.parseLong(app.getString(_ADJUST_INFO_3));
        long ADJUST_INFO_4 = Long.parseLong(app.getString(_ADJUST_INFO_4));

        boolean debug = (0 != (app.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

        String environment = debug ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(app, ADJUST_APP_TOKEN, environment);

        config.setAppSecret(ADJUST_SECRET_ID, ADJUST_INFO_1, ADJUST_INFO_2, ADJUST_INFO_3, ADJUST_INFO_4);
        config.setLogLevel(LogLevel.VERBOSE); // enable all logs
        config.setOnAttributionChangedListener(attributionChangedListener);
        Adjust.onCreate(config);

        app.registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());

//        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
//        String pushToken = status.getSubscriptionStatus().getPushToken();
        Adjust.setPushToken(pushToken, app);

    }

    public static void event(Context context, @StringRes int strid) {
        if (AdmUtils.isContextInvalid(context)) {
            return;
        }
        String eventId = context.getString(strid);
        AdjustEvent adjustEvent = new AdjustEvent(eventId);
        Adjust.trackEvent(adjustEvent);
    }

    private static final class AdjustLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }

        //...
    }
}
