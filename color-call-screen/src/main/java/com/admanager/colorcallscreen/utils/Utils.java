package com.admanager.colorcallscreen.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.annotation.Nullable;

import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.core.AdmUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Locale;

public class Utils {
    private static final String BG_ASSET = "bgs";

    public static void hideBottomNavigationBar(Activity activity) {
        if (AdmUtils.isContextInvalid(activity)) {
            return;
        }
        int visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            visibility = visibility |
                    View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        activity.getWindow().getDecorView().setSystemUiVisibility(visibility);


    }

    public static String toDurationString(Long aLong) {
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", aLong / 3600, (aLong % 3600) / 60, (aLong % 60));
    }

    public static String getNameOfModel(BgModel model) {
        return Uri.parse(model.image).getLastPathSegment();
    }

    public static File getBgFile(Context context, String name) {
        File rootFile = new File(context.getFilesDir() + "/" + BG_ASSET); //
        return new File(rootFile, name);
    }

    public static File getFilesFolder(Context context, BgModel model) {
        String name = getNameOfModel(model);
        File rootFile = new File(context.getFilesDir() + "/" + BG_ASSET); //
        rootFile.mkdirs();

        return new File(rootFile, name);
    }

    public static void copyFile(File sourceFile, File destFile) {
        try {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }

            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            FileChannel source = null;
            FileChannel destination = null;

            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadBgImage(Context context, String url, final DownloadListener listener) {
        RequestBuilder<File> request = Glide.with(context).asFile()
                .load(url)
                .centerCrop();

        request = request.listener(new RequestListener<File>() {
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
        request.submit();
    }

    public interface DownloadListener {
        void downloaded(File file);
    }
}
