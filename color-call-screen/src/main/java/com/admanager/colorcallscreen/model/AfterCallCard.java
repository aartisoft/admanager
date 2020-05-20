package com.admanager.colorcallscreen.model;

import android.content.Intent;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class AfterCallCard {
    private Intent intent;
    @StringRes
    private int title;
    @StringRes
    private int subtitle;
    @DrawableRes
    private int image;

    public AfterCallCard(int title, int image, Intent intent) {
        this.intent = intent;
        this.title = title;
        this.image = image;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(int subtitle) {
        this.subtitle = subtitle;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}