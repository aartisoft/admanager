package com.admanager.popuppromo;

import android.text.TextUtils;

import com.admanager.config.RemoteConfigHelper;

import java.io.Serializable;

class PromoSpecs implements Serializable {

    private boolean enable;
    private String title;
    private String message;
    private String yes;
    //    private String no;
    private String url;
    private String imageUrl;
    private String videoUrl;
    private String logoUrl;

    PromoSpecs(PromoSpecKeys keys) {
        this.enable = RemoteConfigHelper.getConfigs().getBoolean(keys.getEnableKey()) && RemoteConfigHelper.areAdsEnabled();
        this.title = getString(keys.getTitleKey());
        this.message = getString(keys.getMessageKey());
        this.yes = getString(keys.getYesKey());
//        this.no = getString(keys.getNoKey());
        this.url = getString(keys.getUrlKey());
        this.imageUrl = getString(keys.getImageUrlKey());
        this.videoUrl = getString(keys.getVideoUrlKey());
        this.logoUrl = getString(keys.getLogoUrlKey());
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

    public PromoSpecs setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public PromoSpecs setMessage(String message) {
        this.message = message;
        return this;
    }
/*
    public String getNo() {
        return no;
    }

    public PromoSpecs setNo(String no) {
        this.no = no;
        return this;
    }*/

    public String getTitle() {
        return title;
    }

    public PromoSpecs setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PromoSpecs setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getYes() {
        return yes;
    }

    public PromoSpecs setYes(String yes) {
        this.yes = yes;
        return this;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public PromoSpecs setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        return this;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public PromoSpecs setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public PromoSpecs setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(title) &&
                !TextUtils.isEmpty(message) &&
//                !TextUtils.isEmpty(no) &&
                !TextUtils.isEmpty(url) &&
                !TextUtils.isEmpty(yes);
    }

}
