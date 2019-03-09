package com.admanager.popupad;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;

import com.admanager.core.Consts;

import java.io.Serializable;

public class AdSpecKeys implements Serializable {

    private String enableKey;
    private String titleKey;
    private String messageKey;
    private String yesKey;
    private String noKey;
    private String urlKey;
    private String imageUrlKey;
    private String videoUrlKey;
    private String logoUrlKey;

    public String getEnableKey() {
        return enableKey;
    }

    public AdSpecKeys setEnableKey(String enableKey) {
        this.enableKey = enableKey;
        return this;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public AdSpecKeys setTitleKey(String titleKey) {
        this.titleKey = titleKey;
        return this;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public AdSpecKeys setMessageKey(String messageKey) {
        this.messageKey = messageKey;
        return this;
    }

    public String getYesKey() {
        return yesKey;
    }

    public AdSpecKeys setYesKey(String yesKey) {
        this.yesKey = yesKey;
        return this;
    }

    public String getNoKey() {
        return noKey;
    }

    public AdSpecKeys setNoKey(String noKey) {
        this.noKey = noKey;
        return this;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public AdSpecKeys setUrlKey(String urlKey) {
        this.urlKey = urlKey;
        return this;
    }

    public String getImageUrlKey() {
        return imageUrlKey;
    }

    public AdSpecKeys setImageUrlKey(String imageUrlKey) {
        this.imageUrlKey = imageUrlKey;
        return this;
    }

    public String getVideoUrlKey() {
        return videoUrlKey;
    }

    public AdSpecKeys setVideoUrlKey(String videoUrlKey) {
        this.videoUrlKey = videoUrlKey;
        return this;
    }

    public String getLogoUrlKey() {
        return logoUrlKey;
    }

    public AdSpecKeys setLogoUrlKey(String logoUrlKey) {
        this.logoUrlKey = logoUrlKey;
        return this;
    }

    public void validate() {
        if (TextUtils.isEmpty(enableKey)) {
            throw new IllegalArgumentException("enableKey value is null.");
        }
        if (TextUtils.isEmpty(messageKey)) {
            throw new IllegalArgumentException("messageKey value is null.");
        }
        if (TextUtils.isEmpty(noKey)) {
            throw new IllegalArgumentException("noKey value is null.");
        }
        if (TextUtils.isEmpty(titleKey)) {
            throw new IllegalArgumentException("titleKey value is null.");
        }
        if (TextUtils.isEmpty(urlKey)) {
            throw new IllegalArgumentException("urlKey value is null.");
        }
        if (TextUtils.isEmpty(yesKey)) {
            throw new IllegalArgumentException("yesKey value is null.");
        }
    }

    void setDefaultValues(Context context) {
        boolean debug = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

        this.enableKey = checkAndSetDefaultKey(debug, this.enableKey, Consts.PopupAd.DEFAULT_ENABLE_KEY);
        this.messageKey = checkAndSetDefaultKey(debug, this.messageKey, Consts.PopupAd.DEFAULT_MESSAGE_KEY);
        this.noKey = checkAndSetDefaultKey(debug, this.noKey, Consts.PopupAd.DEFAULT_NO_KEY);
        this.titleKey = checkAndSetDefaultKey(debug, this.titleKey, Consts.PopupAd.DEFAULT_TITLE_KEY);
        this.urlKey = checkAndSetDefaultKey(debug, this.urlKey, Consts.PopupAd.DEFAULT_URL_KEY);
        this.yesKey = checkAndSetDefaultKey(debug, this.yesKey, Consts.PopupAd.DEFAULT_YES_KEY);
        this.videoUrlKey = checkAndSetDefaultKey(debug, this.videoUrlKey, Consts.PopupAd.DEFAULT_VIDEO_URL_KEY);
        this.imageUrlKey = checkAndSetDefaultKey(debug, this.imageUrlKey, Consts.PopupAd.DEFAULT_IMAGE_URL_KEY);
        this.logoUrlKey = checkAndSetDefaultKey(debug, this.logoUrlKey, Consts.PopupAd.DEFAULT_LOGO_URL_KEY);
    }

    private String checkAndSetDefaultKey(boolean debug, String firebaseKey, String defaultKey) {
        if (firebaseKey == null) {
            if (debug) {
                Log.i(Consts.TAG, "Firebase Popup Ad is initialized with default firebase keys: " + defaultKey);
            }
            return defaultKey;
        }
        return firebaseKey;
    }
}
