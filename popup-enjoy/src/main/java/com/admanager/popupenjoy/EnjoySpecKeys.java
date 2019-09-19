package com.admanager.popupenjoy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;

import com.admanager.core.Consts;

import java.io.Serializable;

public class EnjoySpecKeys implements Serializable {

    private String enableKey;
    private String titleKey;
    private String yesKey;
    private String noKey;
    private String imageUrlKey;

    public String getEnableKey() {
        return enableKey;
    }

    public EnjoySpecKeys setEnableKey(String enableKey) {
        this.enableKey = enableKey;
        return this;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public EnjoySpecKeys setTitleKey(String titleKey) {
        this.titleKey = titleKey;
        return this;
    }

    public String getYesKey() {
        return yesKey;
    }

    public EnjoySpecKeys setYesKey(String yesKey) {
        this.yesKey = yesKey;
        return this;
    }

    public String getNoKey() {
        return noKey;
    }

    public EnjoySpecKeys setNoKey(String noKey) {
        this.noKey = noKey;
        return this;
    }

    public String getImageUrlKey() {
        return imageUrlKey;
    }

    public EnjoySpecKeys setImageUrlKey(String imageUrlKey) {
        this.imageUrlKey = imageUrlKey;
        return this;
    }

    public void validate() {
        if (TextUtils.isEmpty(enableKey)) {
            throw new IllegalArgumentException("enableKey value is null.");
        }
    }

    void setDefaultValues(Context context) {
        boolean debug = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

        this.enableKey = checkAndSetDefaultKey(debug, this.enableKey, Consts.PopupEnjoy.DEFAULT_ENABLE_KEY);
        this.noKey = checkAndSetDefaultKey(debug, this.noKey, Consts.PopupEnjoy.DEFAULT_NO_KEY);
        this.titleKey = checkAndSetDefaultKey(debug, this.titleKey, Consts.PopupEnjoy.DEFAULT_TITLE_KEY);
        this.yesKey = checkAndSetDefaultKey(debug, this.yesKey, Consts.PopupEnjoy.DEFAULT_YES_KEY);
        this.imageUrlKey = checkAndSetDefaultKey(debug, this.imageUrlKey, Consts.PopupEnjoy.DEFAULT_IMAGE_URL_KEY);
    }

    private String checkAndSetDefaultKey(boolean debug, String firebaseKey, String defaultKey) {
        if (firebaseKey == null) {
            if (debug) {
                Log.i(Consts.TAG, "Enjoy Popup is initialized with default firebase keys: " + defaultKey);
            }
            return defaultKey;
        }
        return firebaseKey;
    }
}
