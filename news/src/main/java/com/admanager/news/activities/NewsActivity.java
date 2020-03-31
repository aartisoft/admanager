package com.admanager.news.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.core.Consts;
import com.admanager.news.NewsApp;
import com.admanager.news.R;

import im.delight.android.webview.AdvancedWebView;

public class NewsActivity extends AppCompatActivity {

    private AdvancedWebView webView;

    public static void start(Context context) {
        Intent intent = new Intent(context, NewsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        webView = findViewById(R.id.web_view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        NewsApp instance = NewsApp.getInstance();
        if (instance != null) {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
            if (instance.title != null) {
                setTitle(instance.title);
            }
            String key = instance.theStartMagazineKey;
            String publisher = instance.theStartMagazinePublisherId;
            String url = String.format("https://www.thestartmagazine.com/feed/summary?key=%s&publisherId=%s&locale=AUTO&isDesktop=false", key, publisher);
            webView.loadUrl(url);
        } else {
            Log.e(Consts.TAG, "*********************************");
            Log.e(Consts.TAG, "You should initialize NEWS module");
            Log.e(Consts.TAG, "*********************************");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        webView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onBackPressed() {
        if (!webView.onBackPressed()) {
            return;
        }
        // ...
        super.onBackPressed();
    }

}