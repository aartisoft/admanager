package com.admanager.sample;

import java.util.HashMap;

/**
 * Created by Gust on 20.12.2017.
 */
public class RCUtils {
    public final static String S1_UNITY_ENABLED = "s1_unity_enabled";
    public final static String S2_UNITY_ENABLED = "s2_unity_enabled";

    public final static String S1_ADMOB_ENABLED = "s1_admob_enabled";
    public final static String S1_ADMOB_ID = "s1_admob_id";

    public static final String S1_FACEBOOK_ENABLED = "s1_facebook_enabled";
    public static final String S1_FACEBOOK_ID = "s1_facebook_id";

    public final static String S2_ADMOB_ENABLED = "s2_admob_enabled";
    public final static String S2_ADMOB_ID = "s2_admob_id";

    public static final String S2_FACEBOOK_ENABLED = "s2_facebook_enabled";
    public static final String S2_FACEBOOK_ID = "s2_facebook_id";

    private static HashMap<String, Object> defaults = null;

    public static HashMap<String, Object> getDefaults() {
        if (defaults == null) {
            defaults = new HashMap<>();
            defaults.put(S1_UNITY_ENABLED, true);
            defaults.put(S2_UNITY_ENABLED, true);

            defaults.put(S1_ADMOB_ENABLED, true);
            defaults.put(S1_FACEBOOK_ENABLED, true);

            defaults.put(S2_ADMOB_ENABLED, true);
            defaults.put(S2_FACEBOOK_ENABLED, true);

            defaults.put(S1_ADMOB_ID, "ca-app-pub-3940256099942544/1033173712"); // TODO
            defaults.put(S2_ADMOB_ID, "ca-app-pub-3940256099942544/1033173712"); // TODO

            defaults.put(S1_FACEBOOK_ID, "YOUR_PLACEMENT_ID"); // TODO
            defaults.put(S2_FACEBOOK_ID, "YOUR_PLACEMENT_ID"); // TODO
        }
        return defaults;
    }
}
