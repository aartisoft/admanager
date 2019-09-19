package com.admanager.popupenjoy;

import android.text.TextUtils;

import com.admanager.config.RemoteConfigHelper;

import java.io.Serializable;

class EnjoySpecs implements Serializable {

    private boolean enable;
    private String title;
    private String yes;
    private String no;
    private String imageUrl;

    EnjoySpecs(EnjoySpecKeys keys) {
        this.enable = RemoteConfigHelper.getConfigs().getBoolean(keys.getEnableKey()) && RemoteConfigHelper.areAdsEnabled();
        this.title = getString(keys.getTitleKey());
        this.yes = getString(keys.getYesKey());
        this.no = getString(keys.getNoKey());
        this.imageUrl = getString(keys.getImageUrlKey());
    }

    private String getString(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return RemoteConfigHelper.getConfigs().getString(key);
    }

    public boolean isEnable() {
        return enable;
    }

    public EnjoySpecs setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public String getNo() {
        return no;
    }

    public EnjoySpecs setNo(String no) {
        this.no = no;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public EnjoySpecs setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getYes() {
        return yes;
    }

    public EnjoySpecs setYes(String yes) {
        this.yes = yes;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public EnjoySpecs setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

}
