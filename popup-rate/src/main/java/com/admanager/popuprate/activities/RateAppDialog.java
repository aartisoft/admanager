package com.admanager.popuprate.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdmUtils;
import com.admanager.core.Consts;
import com.admanager.popuprate.R;
import com.admanager.popuprate.RateApp;
import com.admanager.popuprate.common.Prefs;
import com.admanager.popuprate.listeners.RateClickListener;

public class RateAppDialog extends Dialog implements
        View.OnClickListener,
        RatingBar.OnRatingBarChangeListener {

    private RatingBar ratingBar;
    private TextView btnThanks, dialogTitle, dialogMessage;
    private LinearLayout mRootLayout;
    private Context context;
    private RateClickListener listener;
    private SharedPreferences sharedPref;

    public RateAppDialog(@NonNull Context context, RateClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isStatus()) {
            setContentView(R.layout.adm_dialog_rate);
            initViews();
            configureStyle();
        } else {
            listener.onRated(true);
            dismiss();
        }
    }

    private void saveDialogStatus(boolean status) {
        sharedPref = context.getSharedPreferences(Prefs.ADM_DIALOG_SHOW, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Prefs.ADM_DIALOG_STATUS, status);
        editor.apply();
    }

    private void configureStyle() {
        RateApp instance = RateApp.getInstance();
        if (instance == null) {
            Log.e(Consts.TAG, "init Rate module in Application class");
            dismiss();
        } else {
            if (instance.bgDrawable != 0) {
                mRootLayout.setBackground(ContextCompat.getDrawable(context, instance.bgDrawable));
            }
            if (instance.bgColor != 0) {
                mRootLayout.setBackgroundColor(ContextCompat.getColor(context, instance.bgColor));
            }
            if (instance.textColor != 0) {
                btnThanks.setTextColor(ContextCompat.getColor(context, instance.textColor));
                dialogTitle.setTextColor(ContextCompat.getColor(context, instance.textColor));
                dialogMessage.setTextColor(ContextCompat.getColor(context, instance.textColor));
            }
        }
        if (getWindow() != null)
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initViews() {
        mRootLayout = findViewById(R.id.mRootLayout);
        dialogTitle = findViewById(R.id.dialogTitle);
        dialogMessage = findViewById(R.id.dialogMessage);
        ratingBar = findViewById(R.id.dialogRating);
        btnThanks = findViewById(R.id.btnThanks);
        ratingBar.setOnRatingBarChangeListener(this);
        btnThanks.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnThanks) {
            listener.onRated(false);
            dismiss();
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

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
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
            }
        }
        dismiss();
    }
}