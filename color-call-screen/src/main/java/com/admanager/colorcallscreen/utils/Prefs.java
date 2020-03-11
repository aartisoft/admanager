package com.admanager.colorcallscreen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.colorcallscreen.model.ContactBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Prefs {
    private static final String PREF_FILE_NAME = "pref_file";

    private static final String FLASH = "FLASH";
    private static final String SELECTED_BG = "SELECTED_BG";
    private static final String SELECTED_BG_USER = "SELECTED_BG_USER";
    private static Prefs instance;
    private final SharedPreferences sharedPreferences;

    public Prefs(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static Prefs with(Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    public boolean isFlashEnabled() {
        return sharedPreferences.getBoolean(FLASH, true);
    }

    public void setFlashEnabled(boolean enabled) {
        sharedPreferences
                .edit()
                .putBoolean(FLASH, enabled)
                .apply();
    }

    public String getSelectedBg() {
        return sharedPreferences.getString(SELECTED_BG, null);
    }

    public void setSelectedBg(BgModel model) {
        String name = Utils.getNameOfModel(model);

        sharedPreferences
                .edit()
                .putString(SELECTED_BG, name)
                .apply();
    }

    public void resetSelectedBg() {
        sharedPreferences.edit().remove(SELECTED_BG).apply();
    }

    public boolean isSelectedBg(BgModel model) {
        String selected = getSelectedBg();
        if (TextUtils.isEmpty(selected)) {
            return false;
        }
        String name = Utils.getNameOfModel(model);
        return selected.equals(name);
    }

    public void addSelectedBgForUser(ContactBean c, BgModel bg) {
        if (c == null) {
            setSelectedBg(bg);
            return;
        }
        Gson gson = new Gson();

        HashMap<String, String> map = new HashMap<>();
        String json = sharedPreferences.getString(SELECTED_BG_USER, null);
        if (json != null) {
            map = gson.fromJson(json, HashMap.class);
        }
        String name = Utils.getNameOfModel(bg);

        map.put(c.contactId, name);

        sharedPreferences
                .edit()
                .putString(SELECTED_BG_USER, gson.toJson(map))
                .apply();
    }

    public String getSelectedBgForUser(ContactBean c) {
        String json = sharedPreferences.getString(SELECTED_BG_USER, null);
        if (json == null) {
            return null;
        }
        HashMap<String, String> map = new Gson().fromJson(json, HashMap.class);
        String s = map.get(c.contactId);

        return s;
    }

    public boolean isSelectedBgForUser(ContactBean c, BgModel model) {
        String json = sharedPreferences.getString(SELECTED_BG_USER, null);
        if (json == null) {
            return false;
        }
        HashMap<String, String> map = new Gson().fromJson(json, HashMap.class);

        if (c != null) {
            // check for this user
            String s = map.get(c.contactId);
            return !TextUtils.isEmpty(s);
        }

        // check for any user
        String name = Utils.getNameOfModel(model);
        for (String value : map.values()) {
            if (value.equals(name)) {
                return true;
            }
        }
        return false;

    }

    public List<String> getContactIdListForBg(BgModel model) {
        ArrayList<String> list = new ArrayList<>();

        String json = sharedPreferences.getString(SELECTED_BG_USER, null);
        if (json == null) {
            return list;
        }
        HashMap<String, String> map = new Gson().fromJson(json, HashMap.class);

        String name = Utils.getNameOfModel(model);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(name)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public void removeSelectedBgForUser(ContactBean model) {
        String json = sharedPreferences.getString(SELECTED_BG_USER, null);
        if (json == null) {
            return;
        }
        Gson gson = new Gson();
        HashMap<String, String> map = gson.fromJson(json, HashMap.class);

        map.remove(model.contactId);

        sharedPreferences
                .edit()
                .putString(SELECTED_BG_USER, gson.toJson(map))
                .apply();
    }
}

