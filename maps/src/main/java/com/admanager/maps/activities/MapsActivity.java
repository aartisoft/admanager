package com.admanager.maps.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.core.Consts;
import com.admanager.maps.MapsApp;
import com.admanager.maps.R;

public class MapsActivity extends AppCompatActivity implements View.OnKeyListener {

    private WebView mWebView;
    private ProgressBar mProgress;
    private String BASE_URL = "https://www.google.com/maps/";

    public static void start(Context context) {
        Intent intent = new Intent(context, MapsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initViews();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setTitle(getString(R.string.maps));

        MapsApp instance = MapsApp.getInstance();
        if (instance == null) {
            Log.e(Consts.TAG, "init Maps module in Application class");
            finish();
        } else {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
        }

        webViewConfiguration();
    }

    private void initViews() {
        mWebView = findViewById(R.id.mWebView);
        mProgress = findViewById(R.id.mProgress);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void webViewConfiguration() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDisplayZoomControls(true);
        settings.setUserAgentString(null);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(BASE_URL);
                return super.shouldOverrideUrlLoading(view, request);
            }

            public void onPageFinished(WebView view, String url) {
                mProgress.setVisibility(View.GONE);

            }
        });
        mWebView.loadUrl(BASE_URL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}