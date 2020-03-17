package com.admanager.wastickers.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.wastickers.R;
import com.admanager.wastickers.WastickersApp;
import com.admanager.wastickers.adapters.StickerCategoryAdapter;
import com.admanager.wastickers.utils.PermissionChecker;

public class WAStickersActivity extends AppCompatActivity {
    PermissionChecker permissionChecker;
    RecyclerView recyclerView;
    private MenuItem searchMenuItem;

    public static void start(Context context) {
        Intent intent = new Intent(context, WAStickersActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wastickers);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        permissionChecker = new PermissionChecker(this);
        recyclerView = findViewById(R.id.recyclerView);

        WastickersApp instance = WastickersApp.getInstance();
        if (instance != null) {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
            if (instance.title != null) {
                setTitle(instance.title);
            }
            if (instance.bgColor != 0) {
                recyclerView.setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }
        }

        WastickersApp.loadAndBindTo(this, permissionChecker, recyclerView, instance);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wasticker_search_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchMenuItem = menu.findItem(R.id.sticker_search);
        SearchView search = (SearchView) searchMenuItem.getActionView();
        search.setQueryHint(getString(R.string.sticker_search));
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (recyclerView.getAdapter() instanceof StickerCategoryAdapter) {
                    ((StickerCategoryAdapter) recyclerView.getAdapter()).filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (recyclerView.getAdapter() instanceof StickerCategoryAdapter) {
                    ((StickerCategoryAdapter) recyclerView.getAdapter()).filter(query);
                }
                return false;
            }


        });

        return true;
    }
}