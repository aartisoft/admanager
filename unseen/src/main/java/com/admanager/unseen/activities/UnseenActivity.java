package com.admanager.unseen.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.admanager.unseen.R;
import com.admanager.unseen.UnseenApp;
import com.admanager.unseen.fragments.MessagesFragment;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class UnseenActivity extends AppCompatActivity {

    private static final String TAG = "UnseenActivity";

    public static void start(Context context) {
        Intent intent = new Intent(context, UnseenActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unseen);

        Realm.init(getApplicationContext());
        RealmConfiguration configDefault = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configDefault);

        String c = getIntent().getStringExtra("conversation");
        if (c != null) {
            Intent i = new Intent(this, DetailActivity.class);
            Bundle b = new Bundle();
            b.putString("conversation", c);
            i.putExtras(b);
            startActivity(i);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, MessagesFragment.newInstance())
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        UnseenApp instance = UnseenApp.getInstance();
        if (instance != null) {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top_banners));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom_banners));
            }
            if (instance.title != null) {
                setTitle(instance.title);
            }

            if (instance.bgColor != 0) {
                findViewById(R.id.root).setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }
            if (instance.bgDrawable != 0) {
                findViewById(R.id.root).setBackgroundResource(instance.bgDrawable);
            }

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


}
