package com.admanager.unseen.notiservice.converters;

import android.util.Log;

import com.admanager.unseen.notiservice.models.AppNotification;
import com.admanager.unseen.notiservice.models.Conversation;
import com.admanager.unseen.notiservice.models.Message;
import com.admanager.unseen.notiservice.models.User;

import java.util.List;

import io.realm.Realm;

/**
 * Created by a on 28.03.2017.
 */
public class FBookConverter extends BaseConverter {
    private static final String TAG = "FBookConverter";

    @Override
    public Conversation saveAppNotification(Realm realm, AppNotification fbn) {
        if (containsAnyColon(fbn.messages)) {
            Log.i(TAG, "Single group (title: " + fbn.title + ")");
            return getSingleGroup(realm, fbn);
        } else {
            Log.i(TAG, "Single user (title: " + fbn.title + ")");
            return getSingleUser(realm, fbn);
        }
    }

    @Override
    public String getType() {
        return "f";
    }

    @Override
    public String getTitle() {
        return "Facebook";
    }

    private Conversation getSingleGroup(Realm realm, AppNotification fbn) {
        List<String> list = fbn.messages;
        Conversation conversation = getConversation(realm, fbn.title);
        for (String msgLine : list) {
            String[] split = msgLine.split(":", 2);
            String username = split[0];
            String message = split[1];
            Message pm = createMessage(realm, getUser(realm, username), message, fbn.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

    private Conversation getSingleUser(Realm realm, AppNotification fbn) {
        List<String> list = fbn.messages;
        Conversation conversation = getConversation(realm, fbn.title);
        User user = getUser(realm, fbn.title);
        for (String a : list) {
            Message pm = createMessage(realm, user, a, fbn.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }


}
