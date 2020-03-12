package com.admanager.unseen.notiservice.models;

import com.admanager.unseen.utils.CustomTextUtils;

import io.realm.RealmObject;

public class User extends RealmObject {
    private String name;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = CustomTextUtils.adjust(name);
    }
}
