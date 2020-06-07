package com.admanager.wastickers.utils;

import android.app.Activity;

import androidx.annotation.WorkerThread;

import com.admanager.utils.AdmFileUtils;
import com.admanager.wastickers.R;
import com.admanager.wastickers.model.PackageModel;
import com.admanager.wastickers.model.Sticker;
import com.admanager.wastickers.model.StickerPack;

import java.util.ArrayList;

public class WasUtils {

    @WorkerThread
    public static StickerPack toWAStickerPack(Activity context, PackageModel model) {
        String tryFileName = AdmFileUtils.nameToFileName(model.name) + ".png";
        final StickerPack pack = new StickerPack(model.id, model.name, context.getString(R.string.app_name), tryFileName, context.getPackageName());

        ArrayList<Sticker> stickers = new ArrayList<>();
        for (int i = 0; i < model.stickers.size(); i++) {
            String sticker = model.stickers.get(i).image;
            String fileName = AdmFileUtils.getLastBitFromUrl(sticker) + ".webp";

            final Sticker s = new Sticker(fileName, new ArrayList<String>());

            s.imageURL = sticker;
            stickers.add(s);
        }
        pack.tryIconURL = model.trayImage;
        pack.setStickers(stickers);

        return pack;
    }

}
