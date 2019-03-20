package com.admanager.core.tutorial;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admanager.core.R;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class AdmTutorialConfiguration {
    private static final String TAG = "AdmTutorialConf";
    boolean hideButton;
    boolean hideLogoAtLastPage;
    boolean reloadAdsPerPage;
    int layout = R.layout.adm_activity_tutorial;
    int pageLayout = R.layout.adm_tutorial_page;
    int buttonTextNext = R.string.adm_tutorial_next;
    int buttonTextLast = R.string.adm_tutorial_goto_app;
    private int textColor;
    private int textSize;
    private int buttonTextSize;
    private int buttonTextColor;
    private int buttonBg;
    private LinearLayout.LayoutParams buttonLayoutParams;
    private int buttonBgColor;
    private int dotsColor;
    private int bgColor;
    private int bg;

    public AdmTutorialConfiguration(Context context) {
    }

    public AdmTutorialConfiguration textColor(@ColorRes int textColor) {
        this.textColor = textColor;
        return this;
    }

    public AdmTutorialConfiguration textSize(@IntRange(from = 8, to = 99) int textSize) {
        this.textSize = textSize;
        return this;
    }

    public AdmTutorialConfiguration buttonTextSize(@IntRange(from = 8, to = 99) int buttonTextSize) {
        this.buttonTextSize = buttonTextSize;
        return this;
    }

    public AdmTutorialConfiguration buttonTextNext(@StringRes int buttonTextNext) {
        this.buttonTextNext = buttonTextNext;
        return this;
    }

    public AdmTutorialConfiguration buttonTextLast(@StringRes int buttonTextLast) {
        this.buttonTextLast = buttonTextLast;
        return this;
    }

    public AdmTutorialConfiguration buttonTextColor(@ColorRes int buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
        return this;
    }

    public AdmTutorialConfiguration buttonLayoutParams(LinearLayout.LayoutParams buttonLayoutParams) {
        this.buttonLayoutParams = buttonLayoutParams;
        return this;
    }

    public AdmTutorialConfiguration buttonBg(@DrawableRes int buttonBg) {
        this.buttonBg = buttonBg;
        return this;
    }

    public AdmTutorialConfiguration buttonBgColor(@ColorRes int buttonBgColor) {
        this.buttonBgColor = buttonBgColor;
        return this;
    }

    public AdmTutorialConfiguration dotsColor(@ColorRes int dotsColor) {
        this.dotsColor = dotsColor;
        return this;
    }

    public AdmTutorialConfiguration bgColor(@ColorRes int bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public AdmTutorialConfiguration bg(@DrawableRes int bg) {
        this.bg = bg;
        return this;
    }

    public AdmTutorialConfiguration hideButton() {
        this.hideButton = true;
        return this;
    }

    public AdmTutorialConfiguration hideLogoAtLastPage() {
        this.hideLogoAtLastPage = true;
        return this;
    }

    public AdmTutorialConfiguration reloadAdsPerPage() {
        this.reloadAdsPerPage = true;
        return this;
    }

    public AdmTutorialConfiguration withCustomLayout(@LayoutRes int customLayout) {
        this.layout = customLayout;
        return this;
    }

    public AdmTutorialConfiguration withCustomPageLayout(@LayoutRes int customPageLayout) {
        this.pageLayout = customPageLayout;
        return this;
    }

    void applyPageLayoutStyle(TextView desc) {
        if (desc != null) {
            if (textColor != 0) {
                desc.setTextColor(ContextCompat.getColor(desc.getContext(), textColor));
            }
            if (textSize != 0) {
                desc.setTextSize(textSize);
            }
        }
    }

    void applyLayoutStyle(LinearLayout root, Button btnNext) {
        if (root != null) {
            if (bgColor != 0) {
                root.setBackgroundColor(ContextCompat.getColor(root.getContext(), bgColor));
            }
            if (bg != 0) {
                root.setBackground(ContextCompat.getDrawable(root.getContext(), bg));
            }
        }
        if (btnNext != null) {
            if (buttonTextSize != 0) {
                btnNext.setTextSize(buttonTextSize);
            }
            if (buttonTextColor != 0) {
                btnNext.setTextColor(ContextCompat.getColor(btnNext.getContext(), buttonTextColor));
            }
            if (buttonBg != 0) {
                btnNext.setBackground(ContextCompat.getDrawable(btnNext.getContext(), buttonBg));
            }
            if (buttonBgColor != 0) {
                btnNext.setBackgroundColor(ContextCompat.getColor(btnNext.getContext(), buttonBgColor));
            }
            if (buttonLayoutParams != null) {
                btnNext.setLayoutParams(buttonLayoutParams);
            }
            if (hideButton) {
                btnNext.setVisibility(View.GONE);
            }
        }
    }

    void applyDotsLayoutStyle(final WormDotsIndicator dots) {
        if (dots != null) {
            if (dotsColor != 0) {
                dots.setDotIndicatorColor(ContextCompat.getColor(dots.getContext(), dotsColor));
                dots.setStrokeDotsIndicatorColor(ContextCompat.getColor(dots.getContext(), dotsColor));
            }
        }
    }
}
