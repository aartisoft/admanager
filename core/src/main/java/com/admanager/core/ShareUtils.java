package com.admanager.core;

import android.content.Context;
import android.content.Intent;

public class ShareUtils {

    public static void shareApp(Context context, String text) {
        shareApp(context, text, true);
    }

    public static void shareApp(Context context, String text, boolean appendStoreUrl) {
        try {
            if (appendStoreUrl) {
                text += " http://play.google.com/store/apps/details?id=" + context.getPackageName();
            }

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            i.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(i, context.getString(R.string.share_app_chooser)));
        } catch (Exception ignored) {

        }
    }
}
