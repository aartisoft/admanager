package com.admanager.sample;

import java.util.HashMap;

/**
 * Created by Gust on 20.11.2018.
 */
public class RCUtils {
    public final static String S1_ADMOB_ENABLED = "s1_admob_enabled";
    public static final String S1_FACEBOOK_ENABLED = "s1_facebook_enabled";

    public final static String S2_ADMOB_ENABLED = "s2_admob_enabled";
    public static final String S2_FACEBOOK_ENABLED = "s2_facebook_enabled";

    public final static String MAIN_ADMOB_ENABLED = "main_admob_enabled";
    public static final String MAIN_FACEBOOK_ENABLED = "main_facebook_enabled";

    public static final String ONEXIT_UNITY_ENABLED = "onexit_unity_enabled";
    public static final String ONRESUME_UNITY_ENABLED = "onresume_unity_enabled";

    public final static String S1_ADMOB_ID = "s1_admob_id";
    public final static String S2_ADMOB_ID = "s2_admob_id";
    public final static String MAIN_ADMOB_ID = "main_admob_id";

    public static final String S1_FACEBOOK_ID = "s1_facebook_id";
    public static final String S2_FACEBOOK_ID = "s2_facebook_id";
    public static final String MAIN_FACEBOOK_ID = "main_facebook_id";

    public static final String NATIVE_FACEBOOK_ENABLED = "native_facebook_enabled";
    public static final String NATIVE_FACEBOOK_ID = "native_facebook_id";

    public static final String NATIVE_ADMOB_ENABLED = "native_admob_enabled";
    public static final String NATIVE_ADMOB_ID = "native_admob_id";

    public static final String MAIN_6SEC_FACEBOOK_ENABLED = "main_6sec_facebook_enabled";
    public static final String MAIN_6SEC_FACEBOOK_ID = "main_6sec_facebook_id";

    public static final String ADMOB_BANNER_ENABLED = "admob_banner_enabled";
    public static final String ADMOB_BANNER_ID = "admob_banner_id";

    public static final String FACEBOOK_BANNER_ENABLED = "facebook_banner_enabled";
    public static final String FACEBOOK_BANNER_ID = "facebook_banner_id";

    public static final String CUSTOM_BANNER_ENABLED = "custom_banner_enabled";
    public static final String CUSTOM_BANNER_IMAGE_URL = "custom_banner_image_url";
    public static final String CUSTOM_BANNER_CLICK_URL = "custom_banner_click_url";

    public static final String NOTIF_ENABLE = "notif_enable";
    public static final String NOTIF_DAYS = "notif_days";
    public static final String NOTIF_TITLE = "notif_title";
    public static final String NOTIF_TICKER = "notif_ticker";
    public static final String NOTIF_CONTENT = "notif_content";

    private static HashMap<String, Object> defaults = null;

    public static HashMap<String, Object> getDefaults() {
        if (defaults == null) {
            defaults = new HashMap<>();
            defaults.put(S1_ADMOB_ENABLED, true);
            defaults.put(S1_FACEBOOK_ENABLED, true);

            defaults.put(S2_ADMOB_ENABLED, true);
            defaults.put(S2_FACEBOOK_ENABLED, true);

            defaults.put(MAIN_ADMOB_ENABLED, true);
            defaults.put(MAIN_FACEBOOK_ENABLED, true);

            defaults.put(ONEXIT_UNITY_ENABLED, true);
            defaults.put(ONRESUME_UNITY_ENABLED, true);
            defaults.put(MAIN_6SEC_FACEBOOK_ENABLED, true);

            defaults.put(NATIVE_FACEBOOK_ENABLED, true);
            defaults.put(NATIVE_ADMOB_ENABLED, true);

            defaults.put(FACEBOOK_BANNER_ENABLED, true);
            defaults.put(ADMOB_BANNER_ENABLED, true);
            defaults.put(CUSTOM_BANNER_ENABLED, true);

            defaults.put(S1_ADMOB_ID, "ca-app-pub-3940256099942544/1033173712"); // TODO
            defaults.put(S2_ADMOB_ID, "ca-app-pub-3940256099942544/1033173712"); // TODO
            defaults.put(MAIN_ADMOB_ID, "ca-app-pub-3940256099942544/1033173712"); // TODO
            defaults.put(ADMOB_BANNER_ID, "ca-app-pub-3940256099942544/6300978111"); // TODO
            defaults.put(NATIVE_ADMOB_ID, "ca-app-pub-3940256099942544/2247696110"); // TODO

            defaults.put(S1_FACEBOOK_ID, "YOUR_PLACEMENT_ID"); // TODO
            defaults.put(S2_FACEBOOK_ID, "YOUR_PLACEMENT_ID"); // TODO
            defaults.put(MAIN_FACEBOOK_ID, "YOUR_PLACEMENT_ID"); // TODO
            defaults.put(NATIVE_FACEBOOK_ID, "YOUR_PLACEMENT_ID"); // TODO
            defaults.put(MAIN_6SEC_FACEBOOK_ID, "YOUR_PLACEMENT_ID"); // TODO
            defaults.put(FACEBOOK_BANNER_ID, "YOUR_PLACEMENT_ID"); // TODO

            defaults.put(CUSTOM_BANNER_IMAGE_URL, "https://image.oaking.tk/raw/kwd2i0ndex.gif");
            defaults.put(CUSTOM_BANNER_CLICK_URL, "https://play.google.com/store/apps/details?id=comm.essagechat.listing");

            //NOTIFIC
            defaults.put(NOTIF_ENABLE, false);
            defaults.put(NOTIF_DAYS, 1);
            defaults.put(NOTIF_TITLE, "");
            defaults.put(NOTIF_TICKER, "");
            defaults.put(NOTIF_CONTENT, "");
        }
        return defaults;
    }
}
