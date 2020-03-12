package com.admanager.unseen.notiservice.models;

import io.realm.RealmObject;

/**
 * Created by a on 28.03.2017.
 */
public class Message extends RealmObject {
    private User user;
    private String message;
    private long time;

    public Message() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
