package com.admanager.unseen.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by a on 29.03.2017.
 */
public class FontableTextView extends TextView {

    public FontableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        String ttfName = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "ttf_name");
        Typeface font = FontManager.getInstance(context).loadFont(ttfName);
        setTypeface(font);
    }

}