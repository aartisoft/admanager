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
public class WeChatConverter extends BaseConverter {
    private static final String TAG = "WeChatConverter";

    private static String removeNewMessageAlert(String title) {
        return title.replaceAll("\\[.*\\]", "").trim();
    }

    @Override
    public Conversation saveAppNotification(Realm realm, AppNotification wcn) {
        if (containsAnyColon(wcn.messages)) {
            Log.i(TAG, "Single group (title: " + wcn.title + ")");
            return getSingleGroup(realm, wcn);
        } else {
            Log.i(TAG, "Single user (title: " + wcn.title + ")");
            return getSingleUser(realm, wcn);
        }
    }

    @Override
    public String getType() {
        return "wc";
    }

    @Override
    public String getTitle() {
        return "WeChat";
    }

    private Conversation getSingleGroup(Realm realm, AppNotification wcn) {
        List<String> list = wcn.messages;
        String title = wcn.title;
        title = removeNewMessageAlert(title);
        Conversation conversation = getConversation(realm, title);
        for (String msgLine : list) {
            String[] split = msgLine.split(":", 2);
            String username = removeNewMessageAlert(split[0]);
            String message = removeNewMessageAlert(split[1]);
            Message pm = createMessage(realm, getUser(realm, username), message, wcn.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

    private Conversation getSingleUser(Realm realm, AppNotification wcn) {
        List<String> list = wcn.messages;
        String uname = wcn.title;
        uname = removeNewMessageAlert(uname);

        Conversation conversation = getConversation(realm, uname);
        User user = getUser(realm, uname);
        for (String a : list) {
            Message pm = createMessage(realm, user, a, wcn.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

}