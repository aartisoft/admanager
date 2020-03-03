package com.admanager.gifs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.admanager.core.AdmUtils;
import com.admanager.core.Consts;
import com.admanager.gifs.GifsApp;
import com.admanager.gifs.R;
import com.admanager.gifs.adapters.CategoryAdapter;
import com.admanager.gifs.adapters.GifsAdapter;
import com.admanager.gifs.utils.GifDialog;
import com.admanager.gifs.utils.PermissionChecker;
import com.giphy.sdk.core.models.Category;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListCategoryResponse;
import com.giphy.sdk.core.network.response.ListMediaResponse;

import java.util.List;

public class GifsActivity extends AppCompatActivity implements GifsAdapter.SelectedListener {
    PermissionChecker permissionChecker;
    RecyclerView rvSubCategory;
    RecyclerView rvCategory;
    View root;
    RecyclerView rvGifs;
    GifsAdapter adapterGifs;
    CategoryAdapter adapterSubCategory;
    CategoryAdapter adapterCategory;
    private MenuItem searchMenuItem;
    private GPHApiClient gphApiClient;

    public static void start(Context context) {
        Intent intent = new Intent(context, GifsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifs);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        permissionChecker = new PermissionChecker(this);
        rvSubCategory = findViewById(R.id.rvSubCategory);
        rvCategory = findViewById(R.id.rvCategory);
        rvGifs = findViewById(R.id.rvGifs);
        root = findViewById(R.id.root);
        setTitle(getString(R.string.gifs_powered_by_giphy));

        GifsApp instance = GifsApp.getInstance();
        if (instance != null) {
            gphApiClient = new GPHApiClient(instance.api_key);

            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
            if (instance.bgColor != 0) {
                root.setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }
        } else {
            Log.e(Consts.TAG, "init Gif module in Application class");
            finish();
            return;
        }

        GridLayoutManager grid = new GridLayoutManager(this, 2);
        rvGifs.setLayoutManager(grid);
        adapterGifs = new GifsAdapter(this, this);
        rvGifs.setAdapter(adapterGifs);

        LinearLayoutManager llm1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSubCategory.setLayoutManager(llm1);
        adapterSubCategory = new CategoryAdapter(this, new CategoryAdapter.SelectedListener() {
            @Override
            public void selected(Category category) {
                loadGifs(category.getName());
            }
        });
        rvSubCategory.setAdapter(adapterSubCategory);

        LinearLayoutManager llm2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCategory.setLayoutManager(llm2);
        adapterCategory = new CategoryAdapter(this, new CategoryAdapter.SelectedListener() {
            @Override
            public void selected(Category category) {
                loadSubCategory(category.getName());
            }
        });
        rvCategory.setAdapter(adapterCategory);

        loadCategories();
    }

    private void loadCategories() {
        adapterCategory.setLoadingFullScreen();
        gphApiClient.categoriesForGifs(null, null, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(ListCategoryResponse listCategoryResponse, Throwable throwable) {
                if (AdmUtils.isContextInvalid(GifsActivity.this)) {
                    return;
                }
                adapterCategory.loaded();
                if (throwable != null) {
                    return;
                }
                if (listCategoryResponse == null) {
                    return;
                }
                List<Category> categoryList = listCategoryResponse.getData();
                if (categoryList == null || categoryList.size() == 0) {
                    return;
                }

                adapterCategory.setData(categoryList);

                Category category = categoryList.get(0);

                loadSubCategory(category.getName());
            }
        });
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

    private void loadSubCategory(String name) {
        final String categoryFix = name
                .replace("&", "-")
                .replace(" ", "");
        adapterCategory.setSelected(categoryFix);
        adapterSubCategory.setLoadingFullScreen();

        gphApiClient.subCategoriesForGifs(categoryFix, null, null, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(ListCategoryResponse listCategoryResponse, Throwable throwable) {
                if (AdmUtils.isContextInvalid(GifsActivity.this)) {
                    return;
                }
                adapterSubCategory.loaded();
                if (throwable != null) {
                    return;
                }
                if (listCategoryResponse == null) {
                    return;
                }
                List<Category> categoryList = listCategoryResponse.getData();
                if (categoryList == null || categoryList.size() == 0) {
                    return;
                }
                adapterSubCategory.setData(categoryList);

                Category subCategory = categoryList.get(0);

                loadGifs(subCategory.getName());
            }
        });
    }

    private void loadGifs(String name) {
        adapterSubCategory.setSelected(name);
        adapterGifs.setLoadingFullScreen();
        gphApiClient.search(name, MediaType.gif, 50, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse listMediaResponse, Throwable throwable) {
                adapterGifs.loaded();
                if (throwable != null) {
                    return;
                }
                if (listMediaResponse == null) {
                    return;
                }

                List<Media> mediaList = listMediaResponse.getData();
                if (mediaList == null || mediaList.size() == 0) {
                    return;
                }
                adapterGifs.setData(mediaList);
            }
        });

    }

    @Override
    public void selected(final Media media) {
        if (media == null) {
            return;
        }
        if (AdmUtils.isContextInvalid(this)) {
            return;
        }

        permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
            @Override
            public void run() {
                new GifDialog(GifsActivity.this, media)
                        .show();
            }
        });

    }
}