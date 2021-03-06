package com.admanager.unseen.views;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a on 26.04.2017.
 */
public class FontManager {

    private static FontManager instance = null;
    private Map<String, Typeface> fontCache = new HashMap<>();
    private Context mContext;

    private FontManager(Context mContext2) {
        mContext = mContext2;
    }

    public synchronized static FontManager getInstance(Context mContext) {
        if (instance == null) {
            instance = new FontManager(mContext);
        }
        return instance;
    }

    public Typeface loadFont(String font) {
        if (!fontCache.containsKey(font)) {
            fontCache.put(font,
                    Typeface.createFromAsset(mContext.getAssets(), font));
        }
        return fontCache.get(font);
    }
}