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
public class TeleConverter extends BaseConverter {
    private static final String TAG = "TeleConverter";

    private static String removeNewMessageAlert(String title) {
        return title.replaceAll("\\(.*\\)", "").trim();
    }

    private static boolean isMulti(AppNotification wan) {
        return wan.title.startsWith("Telegram");
    }

    @Override
    public Conversation saveAppNotification(Realm realm, AppNotification tele) {
        if (isMulti(tele)) {
            Log.i(TAG, "Multiple (title: " + tele.title + ")");
            return null; // todo im not sure
        } else if (containsAnyColon(tele.messages)) {
            Log.i(TAG, "Single group (title: " + tele.title + ")");
            return getSingleGroup(realm, tele);
        } else {
            Log.i(TAG, "Single user (title: " + tele.title + ")");
            return getSingleUser(realm, tele);
        }
    }

    @Override
    public String getType() {
        return "t";
    }

    @Override
    public String getTitle() {
        return "Telegram";
    }

    private Conversation getSingleGroup(Realm realm, AppNotification tele) {
        List<String> list = tele.messages;
        String title = tele.title;
        title = removeNewMessageAlert(title);
        Conversation conversation = getConversation(realm, title);
        for (String msgLine : list) {
            String[] split = msgLine.split(":", 2);
            String username = split[0];
            String message = split[1];
            Message pm = createMessage(realm, getUser(realm, username), message, tele.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

    private Conversation getSingleUser(Realm realm, AppNotification tele) {
        List<String> list = tele.messages;
        String uname = tele.title;
        uname = removeNewMessageAlert(uname);

        Conversation conversation = getConversation(realm, uname);
        User user = getUser(realm, uname);
        for (String a : list) {
            Message pm = createMessage(realm, user, a, tele.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }
}