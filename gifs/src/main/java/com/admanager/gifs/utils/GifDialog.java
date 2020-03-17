package com.admanager.gifs.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.admanager.gifs.R;
import com.bumptech.glide.Glide;
import com.giphy.sdk.core.models.Media;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class GifDialog extends Dialog implements View.OnClickListener {
    public static final String LATEST_SHARED = "latest_shared_gif.gif";
    private static final String TAG = "GIF_Dialog";
    private final Media media;
    private ImageView img;
    private View root;

    public GifDialog(Activity activity, Media media) {
        super(activity);
        this.media = media;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.gifs_dialog_selected_gif);
        TextView txt_title = findViewById(R.id.txt_title);
        TextView txt_share = findViewById(R.id.txt_share);
        TextView txt_download = findViewById(R.id.txt_download);
        img = findViewById(R.id.img);
        root = findViewById(R.id.root);

        txt_title.setText(media.getTitle());

        txt_share.setOnClickListener(this);
        txt_download.setOnClickListener(this);

        Glide.with(getContext())
                .load(media.getImages().getFixedHeight().getGifUrl())
                .into(img);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_share) {
            share();
        } else if (v.getId() == R.id.txt_download) {
            download();
        }

//        dismiss();
    }

    private void share() {
        share(media);
    }

    private void download() {
        download(media);
    }

    public void download(Media media) {
        download(media, null);
    }

    private void downloadedAlert(File gifFile) {
        //        Uri selectedUri = Uri.fromFile(gifFile);
        Uri selectedUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", gifFile);

        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(selectedUri, "image/*");

        try {
            Snackbar snackbar = Snackbar.make(root, getContext().getString(R.string.gifs_downloaded), Snackbar.LENGTH_LONG);

            if (intent.resolveActivityInfo(getContext().getPackageManager(), 0) != null) {
                snackbar.setAction(getContext().getString(R.string.gifs_open), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getContext().startActivity(intent);
                    }
                }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.addToGallery(getContext(), gifFile);
    }

    private String getFileName(Media media) {
        String fileName = media.getTitle();
        if (fileName == null) {
            fileName = "gif_" + System.currentTimeMillis();
        }
        fileName += ".gif";
        return fileName;
    }

    public void download(final Media media, final Runnable onCompleteListener) {
        if (TextUtils.isEmpty(media.getImages().getFixedHeightDownsampled().getGifUrl())) {
            return;
        }

        final File gifFile = new File(Utils.getDownloadFolder(getContext()), getFileName(media));
        boolean success = Utils.getFileFromImageView(gifFile, img);
        if (success) {
            downloadedAlert(gifFile);
            return;
        }

        new DownloadFileFromURL(media.getImages().getFixedHeightDownsampled().getGifUrl(), getFileName(media), Utils.getDownloadFolder(getContext()), new OnDownloadListener() {
            @Override
            public void onDownloadFinished(String url, String path, String name) {
                downloadedAlert(gifFile);
                if (onCompleteListener != null) {
                    onCompleteListener.run();
                }
            }

            @Override
            public void onDownloadStart(String url) {
                Toast.makeText(getContext(), "Downloading: " + media.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadProgress(String url, int progress) {
                Log.d(TAG, "onDownloadProgress: " + progress);
            }
        }).execute();
    }

    public void share(Media media) {
        final File gifFile = getCacheFile();
        boolean success = Utils.getFileFromImageView(gifFile, img);
        if (success) {
            Utils.shareGif(getContext(), gifFile);
            return;
        }

        if (!TextUtils.isEmpty(media.getImages().getFixedHeightDownsampled().getGifUrl())) {
            download(media, new Runnable() {
                @Override
                public void run() {
                    Utils.shareGif(getContext(), gifFile);
                }
            });
        }
    }

    @NonNull
    private File getCacheFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), LATEST_SHARED);
    }
}

