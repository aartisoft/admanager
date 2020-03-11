package com.admanager.colorcallscreen.service;

import android.text.TextUtils;

import com.admanager.colorcallscreen.model.ContactBean;

import java.io.Serializable;

public final class GsmCall implements Serializable {
    private GsmCall.Status status;
    private ContactBean contactBean;

    public GsmCall(Status status, ContactBean contactBean) {
        this.status = status;
        this.contactBean = contactBean;
    }

    public Status getStatus() {
        return status;
    }

    public GsmCall setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getDisplayName() {
        String display = contactBean.displayName;
        if (TextUtils.isEmpty(display)) {
            display = "Unknown";
        }
        return display;
    }

    public String getNumber() {
        if (TextUtils.isEmpty(contactBean.number)) {
            return "Unknown";
        }
        return contactBean.number;
    }

    public String getPortraitUri() {
        return contactBean.portraitUri;
    }

    @Override
    public String toString() {
        return "GsmCall{" +
                "status=" + status +
                ", displayName='" + contactBean + '\'' +
                '}';
    }

    public enum Status implements Serializable {
        HOLDING,
        CONNECTING,
        DIALING,
        RINGING,
        ACTIVE,
        DISCONNECTED,
        UNKNOWN
    }
}
