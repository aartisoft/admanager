package com.admanager.unseen.notiservice.converters;

import android.util.Log;

import com.admanager.unseen.notiservice.models.AppNotification;
import com.admanager.unseen.notiservice.models.Conversation;
import com.admanager.unseen.notiservice.models.Message;
import com.admanager.unseen.notiservice.models.User;
import com.admanager.unseen.utils.CustomTextUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

/**
 * Created by a on 28.03.2017.
 */
public class WhatsAppConverter extends BaseConverter {
    private static final String TAG = "WhatsAppMsgConverter";

    private static boolean isMulti(AppNotification wan) {
        return wan.title.startsWith("WhatsApp");
    }

    @Override
    public Conversation saveAppNotification(Realm realm, AppNotification wan) {
        if (isMulti(wan)) {
            Log.i(TAG, "Multiple users and groups (title: " + wan.title + ")");
            return saveMulti(realm, wan);
        } else if (containsAnyColon(wan.messages)) {
            Log.i(TAG, "Single group (title: " + wan.title + ")");
            return saveSingleGroup(realm, wan);
        } else if (containsAny(wan.title, "):") && containsAny(wan.title, "(")) {
            Log.i(TAG, "Single group multiple messages (title: " + wan.title + ")");
            return saveSingleGroupMultipleMessages(realm, wan);
        } else if (containsAny(wan.title, ": ")) {
            Log.i(TAG, "Single group first message (title: " + wan.title + ")");
            return saveSingleGroupFirstMessage(realm, wan);
        } else {
            Log.i(TAG, "Single user (title: " + wan.title + ")");
            return saveSingleUser(realm, wan);
        }
    }

    @Override
    public String getType() {
        return "w";
    }

    @Override
    public String getTitle() {
        return "Whatsapp";
    }

    private Conversation saveSingleGroupMultipleMessages(Realm realm, AppNotification wan) {
        String title = wan.title;
        String[] split1 = wan.title.split(":");
        if (split1.length > 1) {
            String s = split1[0];

            Pattern PATTERN = Pattern.compile("(.*) \\([0-9]+ (.*)\\)(.*)");

            Matcher m = PATTERN.matcher(s);

            if (m.matches()) {
                title = m.group(1);
            }


        }
        List<String> list = wan.messages;
        Conversation conversation = getConversation(realm, title);
        for (String split : list) {
            String[] split2 = split.split(":", 2);
            String groupName = split2[0];
            String message = split2[1];
            Message pm = createMessage(realm, getUser(realm, groupName), message, wan.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

    private Conversation saveSingleGroupFirstMessage(Realm realm, AppNotification wan) {
        String title = wan.title;
        String[] split1 = wan.title.split(":");
        String group = title;
        String user = title;
        if (split1.length == 2) {
            group = split1[0];
            user = split1[0];
        }
        List<String> list = wan.messages;
        Conversation conversation = getConversation(realm, group);
        for (String message : list) {
            Message pm = createMessage(realm, getUser(realm, user), message, wan.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

    private Conversation saveSingleGroup(Realm realm, AppNotification wan) {
        List<String> list = wan.messages;
        Conversation conversation = getConversation(realm, wan.title);
        for (String split : list) {
            String[] split2 = split.split(":", 2);
            String groupName = split2[0];
            String message = split2[1];
            Message pm = createMessage(realm, getUser(realm, groupName), message, wan.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

    private Conversation saveSingleUser(Realm realm, AppNotification wan) {
        List<String> list = wan.messages;
        String title = wan.title;
        String userStr = title;
        String[] split = title.split("@", 2);
        if (split.length >= 2) {
            title = split[1];
            userStr = split[0];
        }
        Conversation conversation = getConversation(realm, title);
        User user = getUser(realm, userStr);
        for (String msg : list) {
            Message pm = createMessage(realm, user, msg, wan.time);
            if (pm != null) {
                conversation.addMessage(pm);
            }
        }
        realm.insertOrUpdate(conversation);
        return conversation;
    }

    private Conversation saveMulti(Realm realm, AppNotification wan) {
        List<String> list = wan.messages;
        Conversation c = null;
        for (String str : list) {
            Message pm;
            String[] split = str.split(":", 2);

            if (split.length < 2) {
                continue;
            }

            String leftSide = CustomTextUtils.adjust(split[0]);
            String message = CustomTextUtils.adjust(split[1]);
            split = leftSide.split("@", 2);
            if (split.length == 2) {  // tel @ group : message
                String tel = split[0];
                String group = split[1];
                c = getConversation(realm, group.trim());
                pm = createMessage(realm, getUser(realm, tel), message, wan.time);
            } else { // tel: message
                c = getConversation(realm, leftSide);
                pm = createMessage(realm, getUser(realm, leftSide), message, wan.time);
            }
            if (pm != null) {
                c.addMessage(pm);
                realm.insertOrUpdate(c);
            }

        }
        return c;
    }


}
