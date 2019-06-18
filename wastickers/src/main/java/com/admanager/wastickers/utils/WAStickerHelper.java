package com.admanager.wastickers.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.admanager.wastickers.model.Sticker;
import com.admanager.wastickers.model.StickerPack;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WAStickerHelper {

    public static final String WASTICKER_STICKER_CONTENT_PROVIDER = ".wasticker.StickerContentProvider";
    public static final String STICKERS_ASSET = "stickers_asset";
    private static final String TAG = "WAStickerHelper";
    private static final int ADD_PACK = 200;
    private static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    private static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    private static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";

    public static String getContentProviderAuthority(Context context) {
        return context.getPackageName() + WASTICKER_STICKER_CONTENT_PROVIDER;
    }

    public static void downloadStickerImage(Context context, String url, final DownloadListener listener) {
        RequestBuilder<File> load = Glide.with(context).asFile()
                .load(url);
        load = load.listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                listener.downloaded(null);
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                listener.downloaded(resource);
                return false;
            }
        });
        load.submit();
    }

    public static File getFilesFolder(Context context, String identifier, boolean tryIcon, String name) {
        File rootFile = new File(context.getFilesDir() + "/" + STICKERS_ASSET); //
        rootFile.mkdirs();

        String root = rootFile.getAbsolutePath() + "/" + identifier;
        File myDir = new File(root + (tryIcon ? "/try" : ""));
        myDir.mkdirs();
        name = name.replace(" ", "_");
        File file = new File(myDir, name);

        return file;
    }

    public static void adjustSaveAndAddPack(final Activity activity, final StickerPack pack, final WorkCounter.Listener listener) {
        if (pack == null || pack.getStickers() == null || pack.getStickers().size() == 0) {
            Log.e(TAG, "null sticker list ");
            return;
        }

        downloadAll(activity, pack, new WorkCounter.Listener() {
            @Override
            public void completed() {
                // save all
                Hawk.with(activity).put(pack);

                // add to whatsapp
                addStickerPack(activity, pack);

                listener.completed();
            }
        });

    }

    public static void downloadAll(final Activity activity, final StickerPack pack, @NonNull WorkCounter.Listener listener) {
        ArrayList<File> files = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();
        ArrayList<Boolean> tryIcons = new ArrayList<>();

        urls.add(pack.tryIconURL);
        files.add(getFilesFolder(activity, pack.identifier, true, pack.fileName));
        tryIcons.add(true);

        List<Sticker> stickers = pack.getStickers();
        for (int i = 0; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            urls.add(sticker.imageURL);
            files.add(getFilesFolder(activity, pack.identifier, false, sticker.fileName));
            tryIcons.add(false);
        }

        final WorkCounter workCounter = new WorkCounter(files.size(), listener);

        for (int i = 0; i < urls.size(); i++) {
            final String url = urls.get(i);
            final File file = files.get(i);
            if (file.exists()) file.delete();

            final int finalI = i;
            Log.d(TAG, i + " Start download: " + url);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    downloadStickerImage(activity, url, new DownloadListener() {
                        @Override
                        public void downloaded(File resource) {

                            resource.renameTo(file);

                            workCounter.taskFinished();
                            Log.d(TAG, finalI + " Finished: " + "+" + url);
                        }
                    });
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static void addStickerPack(Activity activity, StickerPack stickerPack) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(EXTRA_STICKER_PACK_ID, stickerPack.identifier);
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, getContentProviderAuthority(activity));
        intent.putExtra(EXTRA_STICKER_PACK_NAME, stickerPack.name);
        try {
            activity.startActivityForResult(intent, ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "error", Toast.LENGTH_LONG).show();
        }
    }

    public interface DownloadListener {
        void downloaded(File file);
    }
}
