package com.admanager.unseen.notiservice.converters;

import android.util.Log;

import com.admanager.unseen.notiservice.models.AppNotification;
import com.admanager.unseen.notiservice.models.Conversation;
import com.admanager.unseen.notiservice.models.Message;
import com.admanager.unseen.notiservice.models.User;
import com.admanager.unseen.utils.CustomTextUtils;

import java.util.List;

import io.realm.Realm;

/**
 * Created by a on 28.03.2017.
 */
public abstract class BaseConverter {
    private static final String TAG = "MessageConverter";

    public Conversation convert(final AppNotification wan) {
        try {
            Realm realm = Realm.getDefaultInstance();
            final Conversation[] m = {null};
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    m[0] = saveAppNotification(realm, wan);
                    if (m[0] != null) {
                        m[0].setType(getType());
                    }
                }
            });

            Conversation c = copyFromRealm(realm, m[0]);
            realm.close();
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    protected Conversation copyFromRealm(Realm realm, Conversation conversation) {
        if (conversation != null) {
            return realm.copyFromRealm(conversation);
        }
        return null;
    }

    public abstract Conversation saveAppNotification(Realm realm, AppNotification wan);

    public abstract String getType();

    public abstract String getTitle();

    protected Message createMessage(Realm realm, User user, String message, long time) {
        message = message != null ? message.trim() : "";

        long one_hour_ago = time - 1000 * 60 * 60;
        long count = 0;
        try {
            count = realm.where(Message.class).equalTo("user.name", user.getName()).equalTo("message", message).greaterThanOrEqualTo("time", one_hour_ago).count();
        } catch (Exception e) {
            Log.d(TAG, "Message already saved: " + message);
        }
        if (count > 0) {
            return null;
        }

        Message m = realm.createObject(Message.class);
        m.setUser(user);
        m.setMessage(CustomTextUtils.adjust(message));
        m.setTime(time);
        return m;
    }

    protected Conversation getConversation(Realm realm, String title) {
        title = title != null ? title.trim() : null;
        Conversation name = realm.where(Conversation.class)
                .equalTo("title", title)
                .equalTo("type", getType())
                .findFirst();
        if (name != null) {
            return name;
        }
        Conversation c = realm.createObject(Conversation.class);
        c.setTitle(title);
        c.setType(getType());
        return c;
    }

    protected User getUser(Realm realm, String name) {
        name = name != null ? name.trim() : null;
        User user = realm.where(User.class).equalTo("name", name).findFirst();
        if (user != null) {
            return user;
        }
        User u = realm.createObject(User.class);
        u.setName(name);
        return u;
    }

    protected boolean containsAny(String list, String search) {
        if (list == null) {
            return false;
        }
        return list.contains(search);
    }

    protected boolean containsAnyColon(List<String> list) {
        for (String str : list) {
            if (!str.contains(":")) {
                return false;
            }
            if (str.startsWith(new String(Character.toChars(127908))) || //mic
                    str.startsWith(new String(Character.toChars(127925))) ||//music
                    str.startsWith(new String(Character.toChars(127909))) || //video
                    (str.startsWith(new String(Character.toChars(128247))) && str.matches(".*\\d+.*"))) {
                return false;
            }
            if (!str.startsWith("http://")) {
                if (str.startsWith("https://")) {
                    return false; //todo ben ekledim
                }
            }

        }
        return true;
    }

}
