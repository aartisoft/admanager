package com.admanager.wastickers.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.admanager.wastickers.model.StickerPack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class Hawk {
    private static final String PREF_FILE_NAME = "sticker_pack_pref";

    private static final String PACK_PREF = "pack_pref";

    private static Hawk instance;
    private static Gson gson;

    private final SharedPreferences sharedPreferences;

    private Hawk(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static Hawk with(Context context) {
        if (instance == null) {
            instance = new Hawk(context);
        }
        if (gson == null) {
            gson = new GsonBuilder().create();
        }
        return instance;
    }

    public ArrayList<StickerPack> get() {
        String prefJson = sharedPreferences.getString(PACK_PREF, "");
        if (TextUtils.isEmpty(prefJson)) {
            return new ArrayList<>();
        }
        return gson.fromJson(prefJson, new TypeToken<ArrayList<StickerPack>>() {
        }.getType());
    }

    public void put(StickerPack pack) {
        ArrayList<StickerPack> list = get();

        boolean exist = false;
        for (int i = 0; i < list.size(); i++) {
            StickerPack p = list.get(i);
            if (p.identifier.equals(pack.identifier) && p.identifier.equals(pack.identifier)) {
                list.remove(i);
                list.add(i, pack);
                exist = true;
            }
        }
        if (!exist) {
            list.add(pack);
        }
        String s = gson.toJson(list);
        sharedPreferences.edit()
                .putString(PACK_PREF, s)
                .apply();
    }
}