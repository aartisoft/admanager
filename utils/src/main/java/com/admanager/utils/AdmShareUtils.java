package com.admanager.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class AdmShareUtils {

    public static void shareFile(Context activity, File sharingGifFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        Uri uri = Uri.fromFile(sharingGifFile);
        String packageName = activity.getApplicationContext().getPackageName();
        Uri uri = AdmFileUtils.getUriFromProvider(activity, sharingGifFile);

        shareIntent.setType("image/" + AdmFileUtils.getExtension(sharingGifFile));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + packageName);
        activity.startActivity(Intent.createChooser(shareIntent, ""));
    }

    public static void shareFile(Context activity, Uri sharingGifFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");// + AdmFileUtils.getExtension(sharingGifFile));
//        Uri uri = Uri.fromFile(sharingGifFile);
        String packageName = activity.getApplicationContext().getPackageName();
//        Uri uri = FileProvider.getUriForFile(activity, packageName + ".fileprovider", sharingGifFile);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.putExtra(Intent.EXTRA_STREAM, sharingGifFile);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + packageName);
        activity.startActivity(Intent.createChooser(shareIntent, ""));
    }
}
