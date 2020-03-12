package com.admanager.unseen.notiservice.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by a on 28.03.2017.
 */
public class AppNotification {
    public String title;
    public long time;
    public List<String> messages;

    public AppNotification() {
        this(new ArrayList());
    }

    private AppNotification(List<String> list) {
        this.messages = new ArrayList();
        this.title = null;
        this.messages = list;
        this.time = new Date().getTime();
    }

    public final boolean isValid() {
        return !(this.messages == null || this.messages.size() <= 0 || this.title == null || this.title.equals(""));
    }

    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AppNotification (" + this.time + "): " + this.title);
        for (String str : this.messages) {
            stringBuilder.append("\n --> " + str);
        }
        return stringBuilder.toString();
    }
}
