package com.admanager.wastickers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.admanager.core.AdmUtils;
import com.admanager.wastickers.adapters.StickerCategoryAdapter;
import com.admanager.wastickers.api.StickerService;
import com.admanager.wastickers.model.PackageModel;
import com.admanager.wastickers.utils.PermissionChecker;
import com.admanager.wastickers.utils.Utils;
import com.admanager.wastickers.utils.WAStickerHelper;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WastickersApp {
    private static com.admanager.wastickers.WastickersApp INSTANCE;
    public String title;
    public int iconWA;
    public int iconDownload;
    public Ads ads;
    public int bgColor;

    WastickersApp(Application app, Ads ads, String title, int iconWA, int iconDownload, int bgColor) {
        this.title = title;
        this.iconWA = iconWA;
        this.iconDownload = iconDownload;
        this.ads = ads;
        this.bgColor = bgColor;
    }

    public static com.admanager.wastickers.WastickersApp getInstance() {
        return INSTANCE;
    }

    private static com.admanager.wastickers.WastickersApp init(com.admanager.wastickers.WastickersApp wastickersApp) {
        INSTANCE = wastickersApp;
        return INSTANCE;
    }

    public static void load(final Context context, final LoadListener listener) {
        StickerService.api().getPacks().enqueue(new Callback<List<PackageModel>>() {
            @Override
            public void onResponse(Call<List<PackageModel>> call, Response<List<PackageModel>> response) {
                if (!response.isSuccessful()) {
                    listener.error("No network");
                } else if (response.body() == null || response.body().size() == 0) {
                    listener.error("No Data");
                } else {

                    long totalPopularity = 0;
                    List<PackageModel> body = response.body();
                    for (PackageModel pm : body) {
                        totalPopularity += pm.popularity;
                        pm.isInWhiteList = WhitelistCheck.isWhitelisted(context, pm.id);
                    }

                    if (totalPopularity < 100000) {
                        Collections.shuffle(body);
                    }

                    listener.loaded(body);
                }
            }

            @Override
            public void onFailure(Call<List<PackageModel>> call, Throwable t) {
                listener.error("No network");
            }
        });
    }

    public static void loadAndBindTo(@NonNull Activity activity, @NonNull PermissionChecker permissionChecker, @NonNull RecyclerView recyclerView, WastickersApp configs) {
        loadAndBindTo(activity, permissionChecker, recyclerView, null, null, configs);
    }

    public static void loadAndBindTo(@NonNull Activity activity, @NonNull PermissionChecker permissionChecker, @NonNull RecyclerView recyclerView) {
        loadAndBindTo(activity, permissionChecker, recyclerView, null, null, null);
    }
    public static void loadAndBindTo(@NonNull final Activity activity, @NonNull final PermissionChecker permissionChecker, @NonNull RecyclerView recyclerView, String remoteConfigNativeAdEnableKey, String remoteConfigNativeAdIdKey) {
        loadAndBindTo(activity, permissionChecker, recyclerView, remoteConfigNativeAdEnableKey, remoteConfigNativeAdIdKey, null);
    }

    public static void loadAndBindTo(@NonNull final Activity activity, @NonNull final PermissionChecker permissionChecker, @NonNull RecyclerView recyclerView, String remoteConfigNativeAdEnableKey, String remoteConfigNativeAdIdKey, WastickersApp configs) {
        final StickerCategoryAdapter adapter = new StickerCategoryAdapter(activity, permissionChecker, remoteConfigNativeAdEnableKey, remoteConfigNativeAdIdKey, configs);
        adapter.setOnStickerClickListener(new StickerCategoryAdapter.StickerClickListener() {
            @Override
            public void selected(final StickerCategoryAdapter.StickerPackContainer model) {
                final ImageView imageView = new ImageView(activity);
                Glide.with(imageView.getContext())
                        .load(model.url)
                        .placeholder(Utils.getRandomColoredDrawable())
                        .fitCenter()
                        .override(512, 512)
                        .into(imageView);
                new AlertDialog.Builder(activity)
                        .setTitle(model.packageName)
                        .setView(imageView)
                        .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                download(permissionChecker, activity, model, null);
                            }
                        })
                        .setNeutralButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (AdmUtils.isContextInvalid(activity)) {
                                            return;
                                        }
                                        final File gifFile = Utils.getCacheFile();
                                        boolean success = Utils.getFileFromImageView(gifFile, imageView);
                                        if (success) {
                                            Utils.shareGif(activity, gifFile);
                                            return;
                                        }

                                        download(permissionChecker, activity, model, new DownloadListener() {
                                            @Override
                                            public void downloaded(File file) {
                                                if (AdmUtils.isContextInvalid(activity)) {
                                                    return;
                                                }
                                                Utils.shareGif(activity, file);
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
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        adapter.setLoadingFullScreen();
        load(activity, new LoadListener() {
            @Override
            public void loaded(List<PackageModel> body) {
                adapter.setData(body);
                adapter.loaded();
            }

            @Override
            public void error(String body) {
                adapter.loaded();
            }
        });
    }

    private static void download(PermissionChecker permissionChecker, final Activity activity, final StickerCategoryAdapter.StickerPackContainer model, final DownloadListener downloadListener) {
        permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
            @Override
            public void run() {
                if (AdmUtils.isContextInvalid(activity)) {
                    return;
                }

                File folder = new File(Utils.getDownloadFolder(activity) + "/" + model.packageName);
                folder.mkdirs();

                String lastBitFromUrl = Utils.getLastBitFromUrl(model.url);

                final File file = new File(folder.getAbsolutePath() + "/" + model.packageName + "_" + lastBitFromUrl + ".png");
                if (file.exists()) file.delete();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        WAStickerHelper.downloadStickerImage(activity, model.url, new WAStickerHelper.DownloadListener() {
                            @Override
                            public void downloaded(File resource) {
                                try {
                                    Utils.copyFile(resource, file);

                                    if (!AdmUtils.isContextInvalid(activity)) {
                                        Utils.addToGallery(activity, file);
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

    public interface LoadListener {
        void loaded(List<PackageModel> body);

        void error(String body);
    }

    private interface DownloadListener {
        void downloaded(File file);
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private int iconWA;
        private int iconDownload;
        private Ads ads;
        private int bgColor;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public WastickersApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public WastickersApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public WastickersApp.Builder iconWA(@DrawableRes int iconWA) {
            this.iconWA = iconWA;
            return this;
        }

        public WastickersApp.Builder iconDownload(@DrawableRes int iconDownload) {
            this.iconDownload = iconDownload;
            return this;
        }

        public WastickersApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new Ads();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            WastickersApp.init(new WastickersApp(app, ads, title, iconWA, iconDownload, bgColor));

        }

    }

    public static class Ads {
        public void loadTop(Activity activity, LinearLayout container) {

        }

        public void loadBottom(Activity activity, LinearLayout container) {

        }
    }
}