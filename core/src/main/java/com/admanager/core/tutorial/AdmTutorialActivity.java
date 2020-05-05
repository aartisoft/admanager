package com.admanager.core.tutorial;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.admanager.core.AdManager;
import com.admanager.core.AdManagerBuilder;
import com.admanager.core.AdmUtils;
import com.admanager.core.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public abstract class AdmTutorialActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final String TAG = "AdmTutorialActivity";

    private boolean adLoaded = false;
    private AdManager adManager;

    private ArrayList<View> layouts = new ArrayList<>();

    private LinearLayout adplaceholder;
    private LinearLayout adplaceholderTop;
    private LinearLayout adplaceholderCenter;
    private LinearLayout adplaceholderContainer;
    private LinearLayout adplaceholderTopContainer;
    private LinearLayout adplaceholderCenterContainer;
    private LinearLayout root;
    private Button btnNext;
    private ViewPager viewPager;
    private LayoutInflater layoutInflater;
    private WormDotsIndicator waWormDotsIndicator;
    private AdmTutorialConfiguration configuration;
    private FirebaseAnalytics firebaseAnalytics;
    private boolean displayedFirstPage;
    private int page;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        hideStatusBar();

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create and get configuration
        configuration = configure();

        setContentView(configuration.layout);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = findViewById(R.id.root_layout);
        viewPager = findViewById(R.id.view_pager);
        btnNext = findViewById(R.id.btn_next);
        adplaceholder = findViewById(R.id.adplaceholder);
        adplaceholderTop = findViewById(R.id.adplaceholder_top);
        adplaceholderCenter = findViewById(R.id.adplaceholder_center);
        adplaceholderContainer = findViewById(R.id.adplaceholder_container);
        adplaceholderTopContainer = findViewById(R.id.adplaceholder_top_container);
        adplaceholderCenterContainer = findViewById(R.id.adplaceholder_center_container);
        waWormDotsIndicator = findViewById(R.id.worm_dots_indicator);
        btnNext.setOnClickListener(this);
        configuration.applyRootLayoutStyle(root, 0);
        configuration.applyButtonStyle(btnNext, 0);
        configuration.applyViewPagerPadding(viewPager);

        // Let developer to define pages
        addTutorialPages();
        if (layouts.size() == 0) {
            throw new IllegalStateException("You have to call addPage at least one time in addTutorialPages method!");
        }
        if (configuration.hideButton) {
            addPage(0, 0);
        }

        // initialize View Pager
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        configuration.applyViewPagerTransformer(viewPager);

        waWormDotsIndicator.setViewPager(viewPager);
        configuration.applyDotsLayoutStyle(waWormDotsIndicator);

        waWormDotsIndicator.setVisibility(configuration.hideButton ? View.VISIBLE : View.GONE);

        loadAllAds();
        onPageSelected(0);
    }

    public int getPosition() {
        return page;
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @NonNull
    protected AdmTutorialConfiguration configure() {
        return new AdmTutorialConfiguration(this);
    }

    private void loadAllAds() {
        // load Banner / Native
        loadTopAd((LinearLayout) findViewById(R.id.top_container));
        loadBottomAd((LinearLayout) findViewById(R.id.bottom_container));
        _loadAd(0);

        // Load Inters Adds
        AdManagerBuilder builder = createAdManagerBuilder();

        // Add listener for inters ad
        adManager = builder
                .listener(new AdManager.AListener() {
                    @Override
                    public void initializedAll(List<Boolean> loaded) {
                        AdmTutorialActivity.this.loaded();
                    }
                })
                .build();
    }

    protected abstract void addTutorialPages();

    private void _loadAd(int position) {
        NativePosition pos = configuration.nativePositions[position % configuration.nativePositions.length];
        adplaceholderTopContainer.setVisibility(pos.equals(NativePosition.TOP) ? View.VISIBLE : View.GONE);
        adplaceholderContainer.setVisibility(pos.equals(NativePosition.BOTTOM) ? View.VISIBLE : View.GONE);
        adplaceholderCenterContainer.setVisibility(pos.equals(NativePosition.CENTER) ? View.VISIBLE : View.GONE);
        switch (pos) {
            case TOP:
                loadAd(adplaceholderTop);
                break;
            case BOTTOM:
                loadAd(adplaceholder);
                break;
            case CENTER:
                loadAd(adplaceholderCenter);
                break;
        }
    }

    protected abstract void loadAd(LinearLayout container);

    protected void loadTopAd(LinearLayout container) {
    }

    protected void loadBottomAd(LinearLayout container) {
    }

    protected abstract AdManagerBuilder createAdManagerBuilder();

    protected final void addPage(@StringRes int desc, @DrawableRes int image) {
        addPage(0, desc, image);
    }

    protected final void addPage(@StringRes int title, @StringRes int desc, @DrawableRes int image) {
        View view = layoutInflater.inflate(configuration.pageLayout, null);
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvDesc = view.findViewById(R.id.desc);
        ImageView iv = view.findViewById(R.id.image);

        if (tvDesc == null) {
            throw new IllegalStateException("Define a TextView with id 'desc' into the page layout");
        }
        if (iv == null) {
            throw new IllegalStateException("Define an ImageView with id 'image' into the page layout");
        }

        configuration.applyPageLayoutStyle(tvDesc, tvTitle);
        if (desc != 0) {
            tvDesc.setText(desc);
        }
        if (tvTitle != null) {
            if (title != 0) {
                tvTitle.setText(title);
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
        }

        if (image != 0) {
            iv.setImageDrawable(ContextCompat.getDrawable(this, image));
        }
        if (image == 0 && desc == 0) {
            try {
                ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                iv.setImageDrawable(ContextCompat.getDrawable(this, info.icon));
                iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                int p = (int) AdmUtils.pxToDp(getApplicationContext(), 40);
                iv.setPadding(p, p, p, p);
            } catch (Throwable ignored) {

            }
            tvDesc.setText(R.string.adm_tutorial_redirecting);
            iv.setVisibility(configuration.hideLogoAtLastPage ? View.GONE : View.VISIBLE);
        }
        layouts.add(view);
    }

    private void loaded() {
        adLoaded = true;
        setNextButtonVisibility(viewPager.getCurrentItem());
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        if (adManager != null) {
            adManager.show();
        }
        if (firebaseAnalytics != null) {
            firebaseAnalytics.logEvent("tutorial_completed", null);
        }
    }

    @Override
    public final void onPageSelected(int position) {
        this.page = position;
        configuration.applyRootLayoutStyle(root, position);
        configuration.applyButtonStyle(btnNext, position);
        setNextButtonVisibility(position);

        if (configuration.hideButton) {
            // last Page is used for triggering AdManager
            if (position == layouts.size() - 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        launchHomeScreen();
                    }
                }, 100);
            }
        }

        if ((configuration.nativePositions.length > 1 || configuration.reloadNativeAds) && displayedFirstPage) {
            _loadAd(position);
        }

        if (!displayedFirstPage) {
            displayedFirstPage = true;
        }

        if (firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("page", (position + 1));

            firebaseAnalytics.logEvent("tutorial", bundle);
        }
    }

    private void setNextButtonVisibility(int position) {
        if (!configuration.hideButton) {
            if (position == layouts.size() - 1) {
                btnNext.setText(configuration.buttonTextLast);
                btnNext.setVisibility(adLoaded ? View.VISIBLE : View.INVISIBLE);
            } else {
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setText(configuration.buttonTextNext);
            }
        }
    }

    @Override
    public final void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public final void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public final void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_next) {
            int current = getItem(+1);
            if (current < layouts.size()) {
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        }
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = layouts.get(position);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
