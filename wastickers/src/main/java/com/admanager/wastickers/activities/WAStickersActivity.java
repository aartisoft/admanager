package com.admanager.wastickers.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.core.AdmUtils;
import com.admanager.wastickers.R;
import com.admanager.wastickers.WastickersApp;
import com.admanager.wastickers.WhitelistCheck;
import com.admanager.wastickers.adapters.StickerCategoryAdapter;
import com.admanager.wastickers.adapters.StickerMainCategoryAdapter;
import com.admanager.wastickers.api.StickerService;
import com.admanager.wastickers.model.CategoryModel;
import com.admanager.wastickers.model.PackageModel;
import com.admanager.wastickers.utils.PermissionChecker;
import com.admanager.wastickers.utils.Utils;
import com.admanager.wastickers.utils.WAStickerHelper;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WAStickersActivity extends AppCompatActivity {
    PermissionChecker permissionChecker;
    RecyclerView recyclerView;
    private MenuItem searchMenuItem;
    private CategoryModel category = null;

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
        LinearLayoutManager llm = new LinearLayoutManager(WAStickersActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(llm);

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

        loadAndBindTo();
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
        updateMenu();

        return true;
    }

    public void loadAndBindTo() {
        if (category == null) {
            getSupportActionBar().setTitle(getString(R.string.wastickers));
            loadCategories();
        } else {
            getSupportActionBar().setTitle(category.name);
            loadStickers(category.category);
        }

        updateMenu();
    }

    private void updateMenu() {
        if (searchMenuItem == null) {
            return;
        }
        if (category == null) {
            searchMenuItem.setVisible(false);
        } else {
            searchMenuItem.setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (category != null) {
            category = null;
            loadAndBindTo();
            return;
        }
        super.onBackPressed();
    }

    private void loadCategories() {
        final StickerMainCategoryAdapter adapter = new StickerMainCategoryAdapter(WAStickersActivity.this);
        adapter.setOnCategoryClickListener(new StickerMainCategoryAdapter.CategoryClickListener() {
            @Override
            public void selected(CategoryModel model) {
                category = model;
                loadAndBindTo();
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setLoadingFullScreen();
        StickerService.api().getCategories().enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                if (!response.isSuccessful()) {
//                    listener.error("No network");
                    adapter.loaded();

                } else if (response.body() == null || response.body().size() == 0) {
//                    listener.error("No Data");
                    adapter.loaded();
                } else {

                    long totalPopularity = 0;
                    List<CategoryModel> body = response.body();

                    adapter.setData(body);
                    adapter.loaded();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
//                listener.error("No network");
                adapter.loaded();

            }
        });
    }

    private void loadStickers(String category) {

        final StickerCategoryAdapter adapter = new StickerCategoryAdapter(WAStickersActivity.this, permissionChecker, WastickersApp.getInstance());
        adapter.setOnStickerClickListener(new StickerCategoryAdapter.StickerClickListener() {
            @Override
            public void selected(final StickerCategoryAdapter.StickerPackContainer model) {
                final ImageView imageView = new ImageView(WAStickersActivity.this);
                Glide.with(imageView.getContext())
                        .load(model.url)
                        .placeholder(Utils.getRandomColoredDrawable())
                        .fitCenter()
                        .override(512, 512)
                        .into(imageView);
                new AlertDialog.Builder(WAStickersActivity.this)
                        .setTitle(model.packageName)
                        .setView(imageView)
                        .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                download(permissionChecker, model, null);
                            }
                        })
                        .setNeutralButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (AdmUtils.isContextInvalid(WAStickersActivity.this)) {
                                            return;
                                        }
                                        final File gifFile = Utils.getCacheFile();
                                        boolean success = Utils.getFileFromImageView(gifFile, imageView);
                                        if (success) {
                                            Utils.shareGif(WAStickersActivity.this, gifFile);
                                            return;
                                        }

                                        download(permissionChecker, model, new DownloadListener() {
                                            @Override
                                            public void downloaded(File file) {
                                                if (AdmUtils.isContextInvalid(WAStickersActivity.this)) {
                                                    return;
                                                }
                                                Utils.shareGif(WAStickersActivity.this, file);
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .create()
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.setLoadingFullScreen();
        StickerService.api().getPacks(category).enqueue(new Callback<List<PackageModel>>() {
            @Override
            public void onResponse(Call<List<PackageModel>> call, Response<List<PackageModel>> response) {
                if (!response.isSuccessful()) {
//                    listener.error("No network");
                    adapter.loaded();

                } else if (response.body() == null || response.body().size() == 0) {
//                    listener.error("No Data");
                    adapter.loaded();
                } else {

                    long totalPopularity = 0;
                    List<PackageModel> body = response.body();
                    for (PackageModel pm : body) {
                        totalPopularity += pm.popularity;
                        pm.isInWhiteList = WhitelistCheck.isWhitelisted(WAStickersActivity.this, pm.id);
                    }

                    if (totalPopularity < 100000) {
                        Collections.shuffle(body);
                    }

                    adapter.setData(body);
                    adapter.loaded();
                }
            }

            @Override
            public void onFailure(Call<List<PackageModel>> call, Throwable t) {
//                listener.error("No network");
                adapter.loaded();

            }
        });

    }

    private void download(PermissionChecker permissionChecker, final StickerCategoryAdapter.StickerPackContainer model, final DownloadListener downloadListener) {
        permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
            @Override
            public void run() {
                if (AdmUtils.isContextInvalid(WAStickersActivity.this)) {
                    return;
                }

                File folder = new File(Utils.getDownloadFolder(WAStickersActivity.this) + "/" + model.packageName);
                folder.mkdirs();

                String lastBitFromUrl = Utils.getLastBitFromUrl(model.url);

                final File file = new File(folder.getAbsolutePath() + "/" + model.packageName + "_" + lastBitFromUrl + ".png");
                if (file.exists()) file.delete();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        WAStickerHelper.downloadStickerImage(WAStickersActivity.this, model.url, new WAStickerHelper.DownloadListener() {
                            @Override
                            public void downloaded(File resource) {
                                try {
                                    Utils.copyFile(resource, file);

                                    if (!AdmUtils.isContextInvalid(WAStickersActivity.this)) {
                                        Utils.addToGallery(WAStickersActivity.this, file);
                                    }
                                    if (downloadListener != null) {
                                        downloadListener.downloaded(file);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    private interface DownloadListener {
        void downloaded(File file);
    }

}