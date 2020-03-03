package com.admanager.gifs;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.io.File;
import java.lang.ref.WeakReference;

public class GifsApp {
    private static GifsApp INSTANCE;
    public String title;
    public int iconDownload;
    public Ads ads;
    public int bgColor;
    public String api_key;

    GifsApp(Application app, Ads ads, String api_key, String title, int iconDownload, int bgColor) {
        this.title = title;
        this.api_key = api_key;
        this.iconDownload = iconDownload;
        this.ads = ads;
        this.bgColor = bgColor;
    }

    public static GifsApp getInstance() {
        return INSTANCE;
    }

    private static GifsApp init(GifsApp gifsApp) {
        INSTANCE = gifsApp;
        return INSTANCE;
    }

    /*
        public static void loadAndBindTo(@NonNull final Activity activity, @NonNull final PermissionChecker permissionChecker, @NonNull RecyclerView recyclerView, String remoteConfigNativeAdEnableKey, String remoteConfigNativeAdIdKey, GifsApp configs) {
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
        }*/
    private interface DownloadListener {
        void downloaded(File file);
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private String api_key;
        private int iconDownload;
        private Ads ads;
        private int bgColor;

        public Builder(@NonNull Application context, String api_key) {
            this.context = new WeakReference<>(context.getApplicationContext());
            this.api_key = api_key;
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public GifsApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public GifsApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public GifsApp.Builder iconDownload(@DrawableRes int iconDownload) {
            this.iconDownload = iconDownload;
            return this;
        }

        public GifsApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            GifsApp.init(new GifsApp(app, ads, api_key, title, iconDownload, bgColor));

        }

    }
}