package com.admanager.core;

import android.app.Activity;
import android.widget.LinearLayout;

import java.io.Serializable;

public interface Ads extends Serializable {
    void loadTop(Activity activity, LinearLayout container);

    void loadBottom(Activity activity, LinearLayout container);
}