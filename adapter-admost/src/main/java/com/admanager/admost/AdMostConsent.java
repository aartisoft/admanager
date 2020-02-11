package com.admanager.admost;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.admanager.core.AdmUtils;

public class AdMostConsent {
    private static final String MY_PREFS = "AdMostConsent";
    private static final String PERSONALIZED_ENABLED = "showPersonalizedAds";

    public static void setUserConsent(Activity activity, Boolean value) {

        if (AdmUtils.isContextInvalid(activity)) {
            return;
        }
        SharedPreferences.Editor editor = activity.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(PERSONALIZED_ENABLED, value == null ? "" : value.toString());
        editor.apply();
    }

    public static Boolean getUserConsent(Activity activity) {
        if (AdmUtils.isContextInvalid(activity)) {
            return null;
        }
        SharedPreferences pref = activity.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        String string = pref.getString(PERSONALIZED_ENABLED, "");
        if ("".equals(string)) {
            return null;
        }
        try {
            return Boolean.valueOf(string);
        } catch (Exception e) {
            return null;
        }
    }

    public static void showGDPRDialog(final Activity activity) {
        showGDPRDialog(activity, false);
    }

    public static void showGDPRDialog(final Activity activity, boolean forceToShow) {
        if (AdmUtils.isContextInvalid(activity)) {
            return;
        }
        Boolean userConsent = getUserConsent(activity);
        if (userConsent != null && !forceToShow) {
            return;
        }
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_gdpr_dialog);
        final TextView mainText = dialog.findViewById(R.id.txt_dialog);
        final TextView yes = dialog.findViewById(R.id.btn_yes);
        final TextView no = dialog.findViewById(R.id.btn_no);
        Linkify.addLinks(mainText, Linkify.ALL);
        mainText.setMovementMethod(LinkMovementMethod.getInstance());
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeUserConsentInfo(activity, true); // user accepted
                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeUserConsentInfo(activity, false); // user rejected
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void storeUserConsentInfo(Activity activity, Boolean consent) {
        setUserConsent(activity, consent);
    }
}