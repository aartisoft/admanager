package com.admanager.core;

public class Consts {

    public static final int RC_KEY_SIZE = 2;
    public static final String TAG = "ADM";
    public static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

    public static class IntentClickParam {
        public static final String PERIODIC_NOTIFICATION_SUFFIX = "perinotif_suffix";
        public static final String BOOSTER_NOTIFICATION_CLICKED = "booster_clicked";
        public static final String STATIC_NOTIFICATION_CLICKED = "notification_clicked";
    }

    public static class PeriodicNotification {
        public static final String DEFAULT_ENABLE_KEY = "notif_enable";
        public static final String DEFAULT_DAYS_KEY = "notif_days";
        public static final String DEFAULT_TITLE_KEY = "notif_title";
        public static final String DEFAULT_TICKER_KEY = "notif_ticker";
        public static final String DEFAULT_CONTENT_KEY = "notif_content";

        public static final String DEFAULT_MINS_KEY = "notif_mins";
        public static final String DEFAULT_REPEAT_KEY = "notif_repeat";
    }

    public static class PopupPromo {
        public static final String DEFAULT_ENABLE_KEY = "userad_enable";
        public static final String DEFAULT_MESSAGE_KEY = "userad_message";
        public static final String DEFAULT_NO_KEY = "userad_no";
        public static final String DEFAULT_TITLE_KEY = "userad_title";
        public static final String DEFAULT_URL_KEY = "userad_url";
        public static final String DEFAULT_YES_KEY = "userad_yes";
        public static final String DEFAULT_VIDEO_URL_KEY = "userad_video_url";
        public static final String DEFAULT_IMAGE_URL_KEY = "userad_image_url";
        public static final String DEFAULT_LOGO_URL_KEY = "userad_logo_url";
    }

    public static class PopupEnjoy {
        public static final String DEFAULT_ENABLE_KEY = "enjoy_enable";
        public static final String DEFAULT_TITLE_KEY = "enjoy_title";
        public static final String DEFAULT_YES_KEY = "enjoy_yes";
        public static final String DEFAULT_NO_KEY = "enjoy_no";
        public static final String DEFAULT_IMAGE_URL_KEY = "enjoy_image_url";
    }

    public static class PopupRate {
        public static final String DEFAULT_ENABLE_KEY = "rate_enable";
    }

}
