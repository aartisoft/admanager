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
public class VibeConverter extends BaseConverter {
    private static final String TAG = "VibeConverter";

    @Override
    public Conversation saveAppNotification(Realm realm, AppNotification vin) {
        if (containsAnyColon(vin.messages)) {
            Log.i(TAG, "Single group (title: " + vin.title + ")");
            return getSingleGroup(realm, vin);
        } else {
            Log.i(TAG, "Single user (title: " + vin.title + ")");
            return getSingleUser(realm, vin);
        }
    }

    @Override
    public String getType() {
        return "v";
    }

    @Override
    public String getTitle() {
        return "Viber";
    }

    private Conversation getSingleGroup(Realm realm, AppNotification vin) {
        List<String> list = vin.messages;
        Conversation conversation = getConversation(realm, vin.title);
        for (String msgLine : list) {
            String[] split = msgLine.split(":", 2);
            String username = split[0];
            String message = split[1];
            Message pm = createMessage(realm, getUser(realm, username), message, vin.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

    private Conversation getSingleUser(Realm realm, AppNotification vin) {
        List<String> list = vin.messages;
        Conversation conversation = getConversation(realm, vin.title);
        User user = getUser(realm, vin.title);
        for (String a : list) {
            Message pm = createMessage(realm, user, a, vin.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }
/* todo nasıl yakalarız
*   AppNotification (1523384114562): 4 okunmamış mesaj
                                     --> Gönderen Grup, Dani

    AppNotification (1523384114015): Grup
                                     --> Grup içinde yeni mesajlarınız var

    AppNotification (1523384111708): Dani
                                     --> 6
                                     --> 7

    AppNotification (1523384111859): 3 okunmamış mesaj
                                     --> Gönderen Grup, Dani

*
* */

}
