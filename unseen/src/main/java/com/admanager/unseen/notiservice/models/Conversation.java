package com.admanager.unseen.notiservice.models;

import com.admanager.unseen.utils.CustomTextUtils;

import java.util.HashSet;
import java.util.Set;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Conversation extends RealmObject {
    private String title;
    private RealmList<Message> messages;
    private Message lastMessage;

    @Required
    private String type;

    public Conversation() {
    }

    public void addMessage(Message m) {
        if (getMessages() == null) {
            messages = new RealmList<>();
        }
        getMessages().add(m);
        lastMessage = m;
    }

    public boolean isMultiple() {
        if (getMessages() == null || getMessages().size() == 0) {
            return false;
        }
        Set<User> users = new HashSet<>();
        for (Message m : getMessages()) {
            users.add(m.getUser());
        }
        return users.size() > 1;
    }

    public RealmList<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = CustomTextUtils.adjust(title);
    }

    public String getLastMessageText() {
        String title = getTitle();
        if (lastMessage == null) {
            return "";
        }
        String uname = lastMessage.getUser().getName();

        if (title.equals(uname)) {
            return lastMessage.getMessage();
        }
        return uname + ": " + lastMessage.getMessage();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
