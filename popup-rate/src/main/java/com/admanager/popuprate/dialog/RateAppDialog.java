package com.admanager.popuprate.dialog;

import android.app.AlertDialog;
import android.content.Context;
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

import com.admanager.core.Consts;
import com.admanager.popuprate.R;
import com.admanager.popuprate.RateApp;
import com.admanager.popuprate.listeners.AddRateListener;

public class RateAppDialog extends AlertDialog implements
        View.OnClickListener,
        RatingBar.OnRatingBarChangeListener {

    private RatingBar ratingBar;
    private TextView btnThanks, dialogTitle, dialogMessage;
    private LinearLayout mRootLayout;
    private Context context;
    private AddRateListener listener;

    public RateAppDialog(@NonNull Context context, AddRateListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_dialog_rate);
        initViews();
        configureStyle();
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
            listener.onThanksClick();
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        listener.onRated(ratingBar, rating, fromUser);
    }

}