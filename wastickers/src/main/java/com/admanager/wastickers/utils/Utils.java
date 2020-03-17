package com.admanager.wastickers.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.content.FileProvider;

import com.admanager.core.AdmUtils;
import com.admanager.wastickers.R;
import com.admanager.wastickers.model.PackageModel;
import com.admanager.wastickers.model.Sticker;
import com.admanager.wastickers.model.StickerPack;
import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Random;

public class Utils {

    public static final int DEFAULT_IMG_SIZE = 150;
    private static final String FOLDER_NAME = "Stickers";

    public static GradientDrawable getRandomColoredDrawable() {
        return getRandomColoredDrawable(null, null);
    }

    public static GradientDrawable getRandomColoredDrawable(Integer x, Integer y) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        if (x != null && y != null) {
            shape.setSize(x, y);
        }
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        shape.setColor(color);
        shape.setStroke(3, color);
        return shape;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
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
    }

    public static void addToGallery(Context context, File gifFile) {
        MediaScannerConnection.scanFile(context,
                new String[]{gifFile.getPath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    @NonNull
    private static String nameToFileName(String name) {
        return name.replace(" ", "_");
    }

    public static String getLastBitFromUrl(final String url) {
        String name = url.replaceFirst(".*/([^/?]+).*", "$1");
        int i = name.lastIndexOf(".");
        if (i > 0) {
            name = name.substring(0, i);
        }
        return name;
    }

    public static boolean getFileFromImageView(File gifFile, ImageView image) {
        Drawable.ConstantState constantState = image.getDrawable().getConstantState();
        if (constantState == null) {
            return false;
        }

        Drawable drawable = constantState.newDrawable().mutate();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(gifFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                return true;
            } catch (IOException e) {
                Log.e("app", e.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } else if (drawable instanceof GifDrawable) {
            ByteBuffer byteBuffer2 = ((GifDrawable) drawable).getBuffer();

            try {
                byteBuffer2.rewind();
                byte[] arr = new byte[byteBuffer2.remaining()];
                byteBuffer2.get(arr);
                FileOutputStream output = new FileOutputStream(gifFile);
                output.write(arr, 0, arr.length);
                output.close();
                return true;

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getDownloadFolder(Activity activity) {
        String folderName = FOLDER_NAME;
        if (!AdmUtils.isContextInvalid(activity)) {
            folderName = getApplicationName(activity);
        }
        StringBuilder s = new StringBuilder();
        String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        s.append(absolutePath);
        if (!absolutePath.endsWith("/")) {
            s.append("/");
        }
        s.append(folderName + "/");
        String s1 = s.toString();

        new File(s1).mkdirs();

        return s1;
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    @WorkerThread
    public static StickerPack toWAStickerPack(Activity context, PackageModel model) {
        String tryFileName = nameToFileName(model.name) + ".png";
        final StickerPack pack = new StickerPack(model.id, model.name, context.getString(R.string.app_name), tryFileName, context.getPackageName());

        ArrayList<Sticker> stickers = new ArrayList<>();
        for (int i = 0; i < model.stickers.size(); i++) {
            String sticker = model.stickers.get(i).image;
            String fileName = getLastBitFromUrl(sticker) + ".webp";

            final Sticker s = new Sticker(fileName, new ArrayList<String>());

            s.imageURL = sticker;
            stickers.add(s);
        }
        pack.tryIconURL = model.trayImage;
        pack.setStickers(stickers);

        return pack;
    }

    public static File getCacheFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "latest_shared.png");
    }

    private static String getExtension(File file) {
        if (file == null) {
            return "*";
        }

        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            return file.getName().substring(i + 1);
        }
        return "*";
    }

    public static void shareGif(Activity activity, File sharingGifFile) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/" + getExtension(sharingGifFile));
//        Uri uri = Uri.fromFile(sharingGifFile);
        String packageName = activity.getApplicationContext().getPackageName();
        Uri uri = FileProvider.getUriForFile(activity, packageName + ".fileprovider", sharingGifFile);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + packageName);
        activity.startActivity(Intent.createChooser(shareIntent, ""));
    }
}
