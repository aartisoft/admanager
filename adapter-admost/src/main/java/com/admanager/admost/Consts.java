package com.admanager.admost;

import android.app.Activity;

import admost.sdk.base.AdMost;
import admost.sdk.base.AdMostConfiguration;

public class Consts {
    public static final int ID_SIZE = 36;

    //Test Ids
    public final static String TEST_APP_ID = "6cc8e89a-b52a-4e9a-bb8c-579f7ec538fe";
    public final static String TEST_BANNER_ZONE = "86644357-21d0-45a4-906a-37262461df65";
    public final static String TEST_FULLSCREEN_ZONE = "f99e409b-f9ab-4a2e-aa9a-4d143e6809ae";
    public final static String TEST_VIDEO_ZONE = "88cfcfd0-2f8c-4aba-9f36-cc0ac99ab140";
    public final static String TEST_OFFERWALL = "fa1072e4-afcf-49b6-a919-1ab1ab1b0aa9";
    public final static String TEST_NATIVE_ZONE = "0155f3d0-5de1-4b10-a48e-8d1d069436b9";

    public static void initAdMost(Activity activity, String appId) {
        AdMostConfiguration.Builder configuration = new AdMostConfiguration.Builder(activity, appId);

        Boolean userConsent = AdMostConsent.getUserConsent(activity);
        if (userConsent != null) {
            configuration.setUserConsent(userConsent);
        }

        AdMost.getInstance().init(configuration.build());
    }
}