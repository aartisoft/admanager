package com.admanager.core.tutorial;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

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
    boolean moveAdToTop;
    private int textColor;
    private int textSize;
    private int titleColor;
    private int titleSize;
    private int buttonTextSize;
    private int buttonTextColor;
    private int buttonBg;
    private LinearLayout.LayoutParams buttonLayoutParams;
    private int buttonBgColor;
    private int dotsColor;
    private boolean reverseDrawingOrder;
    private ViewPager.PageTransformer transformer;
    private int[] bgColor;
    private int[] bg;
    private int[] viewPagerPadding;

    public AdmTutorialConfiguration(Context context) {
    }

    public AdmTutorialConfiguration textColor(@ColorRes int textColor) {
        this.textColor = textColor;
        return this;
    }

    public AdmTutorialConfiguration moveAdToTop() {
        this.moveAdToTop = true;
        return this;
    }

    public AdmTutorialConfiguration textSize(@IntRange(from = 8, to = 99) int textSize) {
        this.textSize = textSize;
        return this;
    }

    public AdmTutorialConfiguration titleColor(@ColorRes int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public AdmTutorialConfiguration titleSize(@IntRange(from = 8, to = 99) int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public AdmTutorialConfiguration viewPagerPadding(@IntRange(from = 0, to = 24) int left,
                                                     @IntRange(from = 0, to = 24) int top,
                                                     @IntRange(from = 0, to = 24) int right,
                                                     @IntRange(from = 0, to = 24) int bottom) {
        this.viewPagerPadding = new int[]{left, top, right, bottom};
        return this;
    }

    public AdmTutorialConfiguration viewPagerPadding(@IntRange(from = 0, to = 24) int leftRight,
                                                     @IntRange(from = 0, to = 24) int topBottom) {
        this.viewPagerPadding = new int[]{leftRight, topBottom, leftRight, topBottom};
        return this;
    }

    public AdmTutorialConfiguration viewPagerPadding(@IntRange(from = 0, to = 24) int all) {
        this.viewPagerPadding = new int[]{all, all, all, all};
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

    public AdmTutorialConfiguration setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        this.reverseDrawingOrder = reverseDrawingOrder;
        this.transformer = transformer;
        return this;
    }

    public AdmTutorialConfiguration dotsColor(@ColorRes int dotsColor) {
        this.dotsColor = dotsColor;
        return this;
    }

    public AdmTutorialConfiguration bgColor(@ColorRes int... bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public AdmTutorialConfiguration bg(@DrawableRes int... bg) {
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

    void applyPageLayoutStyle(TextView desc, TextView title) {
        if (desc != null) {
            if (textColor != 0) {
                desc.setTextColor(ContextCompat.getColor(desc.getContext(), textColor));
            }
            if (textSize != 0) {
                desc.setTextSize(textSize);
            }
        }
        if (title != null) {
            if (titleColor != 0) {
                title.setTextColor(ContextCompat.getColor(title.getContext(), titleColor));
            }
            if (titleSize != 0) {
                title.setTextSize(titleSize);
            }
        }
    }

    void applyRootLayoutStyle(LinearLayout root, int position) {
        if (root == null) {
            return;
        }
        if (bgColor != null && bgColor.length > 0) {
            int val = bgColor[position % bgColor.length];
            animateColor(root, ContextCompat.getColor(root.getContext(), val));
        }
        if (bg != null && bg.length > 0) {
            int val = bg[position % bg.length];
            root.setBackground(ContextCompat.getDrawable(root.getContext(), val));
        }
    }

    private void animateColor(final LinearLayout root, int colorTo) {
        int colorFrom = Color.TRANSPARENT;
        Drawable background = root.getBackground();
        if (background instanceof ColorDrawable) {
            colorFrom = ((ColorDrawable) background).getColor();
        }
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                root.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    void applyButtonStyle(Button btnNext, int position) {
        if (btnNext == null) {
            return;
        }
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

    void applyDotsLayoutStyle(final WormDotsIndicator dots) {
        if (dots != null) {
            if (dotsColor != 0) {
                dots.setDotIndicatorColor(ContextCompat.getColor(dots.getContext(), dotsColor));
                dots.setStrokeDotsIndicatorColor(ContextCompat.getColor(dots.getContext(), dotsColor));
            }
        }
    }

    void applyViewPagerTransformer(ViewPager viewPager) {
        if (transformer == null) {
            return;
        }
        viewPager.setPageTransformer(reverseDrawingOrder, transformer);
    }

    void applyViewPagerPadding(ViewPager viewPager) {
        if (viewPager != null && viewPagerPadding != null && viewPagerPadding.length == 4) {
            viewPager.setPadding(viewPagerPadding[0], viewPagerPadding[1], viewPagerPadding[2], viewPagerPadding[3]);
        }
    }
}
