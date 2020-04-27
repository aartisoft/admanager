package com.admanager.popuprate.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RatingBar;
import android.widget.Toast;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdmUtils;
import com.admanager.core.Consts;
import com.admanager.popuprate.R;
import com.admanager.popuprate.common.Prefs;
import com.admanager.popuprate.listeners.AddRateListener;
import com.admanager.popuprate.listeners.RateClickListener;

public class RateDialog implements AddRateListener {

    RateAppView rateAppView;
    private Context context;
    private RateClickListener listener;
    private SharedPreferences sharedPref;

    public RateDialog(Context context, RateClickListener listener) {
        this.context = context;
        this.listener = listener;
        rateAppView = new RateAppView(context, this);
    }

    public void show() {
        if (isStatus()) {
            rateAppView.show();
        } else {
            rateAppView.dismiss();
            listener.onRated(false);
        }
    }


    private boolean isStatus() {
        SharedPreferences settings = context.getSharedPreferences(Prefs.ADM_DIALOG_SHOW, Context.MODE_PRIVATE);
        boolean localEnabled = settings.getBoolean(Prefs.ADM_DIALOG_STATUS, true);
        boolean dialogStatus = RemoteConfigHelper.getConfigs().getBoolean(Consts.PopupRate.DEFAULT_ENABLE_KEY);
        if (dialogStatus) {
            return localEnabled;
        } else {
            return false;
        }
    }

    private void saveDialogStatus(boolean status) {
        sharedPref = context.getSharedPreferences(Prefs.ADM_DIALOG_SHOW, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Prefs.ADM_DIALOG_STATUS, status);
        editor.apply();
    }

    @Override
    public void onRated(RatingBar ratingBar, float rating, boolean fromUser) {
        if (fromUser) {
            if (rating >= 4) {
                if (isStatus()) {
                    listener.onRated(true);
                    saveDialogStatus(false);
                    AdmUtils.openApp(context, context.getPackageName());
                } else {
                    listener.onRated(false);
                }
            } else {
                saveDialogStatus(true);
                listener.onRated(false);
                toast();
            }
        }
        rateAppView.dismiss();
    }

    @Override
    public void onThanksClick() {
        listener.onRated(false);
        rateAppView.dismiss();
    }

    private void toast() {
        Toast.makeText(context, context.getResources().getString(R.string.popup_rate_us_thanks), Toast.LENGTH_SHORT).show();
    }
}
