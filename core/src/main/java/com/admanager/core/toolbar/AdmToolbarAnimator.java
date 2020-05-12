package com.admanager.core.toolbar;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

public class AdmToolbarAnimator {
    private Toolbar toolbar;
    private String badgeText;
    private Animation animate;

    public AdmToolbarAnimator(@NonNull Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public AdmToolbarAnimator animate() {
        return this.animate(400, 20000);
    }

    public AdmToolbarAnimator animate(@Size(min = 1, max = 10000) long duration, @Size(min = 100) long totalTime) {
        this.animate = new ScaleAnimation(
                1f, 1.1f, // Start and end values for the X axis scaling
                1f, 1.1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
        this.animate.setFillAfter(true); // Needed to keep the result of the animation

        this.animate.setDuration(duration); //You can manage the blinking time with this parameter
        this.animate.setRepeatMode(Animation.REVERSE);
        this.animate.setRepeatCount((int) (totalTime / this.animate.getDuration()));
        ;
        return this;
    }

    public AdmToolbarAnimator setBadgeText(String badgeText) {
        this.badgeText = badgeText;
        return this;
    }

    public AdmToolbarAnimator animate(Animation animate) {
        this.animate = animate;
        return this;
    }

    public void start() {
        if (animate != null) {
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                if (toolbar.getChildAt(i) instanceof AppCompatImageButton) {
                    AppCompatImageButton icon = (AppCompatImageButton) toolbar.getChildAt(i);
                    Drawable d = icon.getDrawable();
                    if (d instanceof DrawerArrowDrawable) {
                        icon.startAnimation(animate);
                    }
                }
            }
        }
        if (badgeText != null) {
            BadgeDrawerArrowDrawable dd = new BadgeDrawerArrowDrawable(toolbar.getContext());
            dd.setText(badgeText);
            toolbar.setNavigationIcon(dd);
        }
    }

}
