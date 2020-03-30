package com.admanager.popuprate.listeners;

import android.widget.RatingBar;

public interface AddRateListener {

    void onRated(RatingBar ratingBar, float rating, boolean fromUser);

    void onThanksClick();
}
