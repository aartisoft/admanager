package com.admanager.colorcallscreen.model;

import java.io.Serializable;

public class ContactBean implements Serializable {
    public boolean checked;
    public String chosenTheme;
    public String contactId;
    public String displayName;
    public String number;
    public String portraitUri;

    public ContactBean(String contactId, String displayName, String number, String portraitUri) {
        this.contactId = contactId;
        this.displayName = displayName;
        if (number == null) {
            number = "";
        }
        this.number = number;
        if (portraitUri == null) {
            portraitUri = "";
        }
        this.portraitUri = portraitUri;
        this.chosenTheme = null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ContactBean)) {
            return false;
        }
        ContactBean contactBean = (ContactBean) obj;
        return this.contactId != null ? this.contactId.equals(contactBean.contactId) : contactBean.contactId == null;
    }

    public int hashCode() {
        return this.contactId != null ? this.contactId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ContactBean{" +
                "checked=" + checked +
                ", chosenTheme='" + chosenTheme + '\'' +
                ", contactId='" + contactId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", number='" + number + '\'' +
                ", portraitUri='" + portraitUri + '\'' +
                '}';
    }
}
