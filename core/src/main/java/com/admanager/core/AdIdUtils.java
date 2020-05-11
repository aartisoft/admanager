package com.admanager.core;

import android.text.TextUtils;

import com.admanager.config.RemoteConfigHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public class AdIdUtils {

    private static CopyOnWriteArraySet<String> idmap = new CopyOnWriteArraySet<>();

    public static String pickAnId(String rcKeyForId) {
        String idstr = RemoteConfigHelper.getConfigs().getString(rcKeyForId);
        if (TextUtils.isEmpty(idstr)) {
            return "";
        }
        String[] split = idstr.split(",");
        if (split.length == 0) {
            return "";
        }
        List<String> ids = Arrays.asList(split);

        Collections.shuffle(ids);

        for (String id : ids) {
            id = id.trim();
            if (!idmap.contains(id)) {
                idmap.add(id);
                return id;
            }
        }
        idmap = new CopyOnWriteArraySet<>();

        String id = ids.get(0).trim();
        idmap.add(id);

        return id;
    }
}
