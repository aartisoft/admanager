package com.admanager.wastickers.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdmUtils;
import com.admanager.recyclerview.AdmAdapterConfiguration;
import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BaseAdapterWithAdmobNative;
import com.admanager.recyclerview.BindableViewHolder;
import com.admanager.wastickers.R;
import com.admanager.wastickers.WastickersApp;
import com.admanager.wastickers.WhitelistCheck;
import com.admanager.wastickers.model.PackageModel;
import com.admanager.wastickers.model.StickerModel;
import com.admanager.wastickers.model.StickerPack;
import com.admanager.wastickers.utils.PermissionChecker;
import com.admanager.wastickers.utils.Utils;
import com.admanager.wastickers.utils.WAStickerHelper;
import com.admanager.wastickers.utils.WorkCounter;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StickerCategoryAdapter extends BaseAdapterWithAdmobNative<PackageModel, StickerCategoryAdapter.StickerPackViewHolder> {
    public static final String TAG = "GifCategoryAdapter";
    private final PermissionChecker permissionChecker;
    private final WastickersApp configs;
    private StickerClickListener stickerClickListener;
    private List<PackageModel> originalList;

    public StickerCategoryAdapter(Activity activity, @NonNull PermissionChecker permissionChecker, String remoteConfigEnableKey, String remoteConfigIdKey, WastickersApp configs) {
        super(activity, R.layout.item_image_group, null, showNative(remoteConfigEnableKey), nativeId(remoteConfigIdKey));
        this.permissionChecker = permissionChecker;
        this.configs = configs;
    }

    public StickerCategoryAdapter(Activity activity, PermissionChecker permissionChecker, WastickersApp configs) {
        this(activity, permissionChecker, "", "", configs);
    }

    private static boolean showNative(String remoteConfigEnableKey) {
        return RemoteConfigHelper.getConfigs().getBoolean(remoteConfigEnableKey) && RemoteConfigHelper.areAdsEnabled();
    }

    private static String nativeId(String remoteConfigIdKey) {
        return RemoteConfigHelper.getConfigs().getString(remoteConfigIdKey);
    }

    @Override
    protected AdmAdapterConfiguration<NativeType> configure() {
        return new AdmAdapterConfiguration<NativeType>()
                .density(2);
    }

    @Override
    public StickerPackViewHolder createViewHolder(View view) {
        StickerPackViewHolder vh = new StickerPackViewHolder(view);
        if (configs != null) {
            if (configs.iconDownload != 0) {
                vh.download.setBackgroundResource(configs.iconDownload);
            }
            if (configs.iconWA != 0) {
                vh.add_to_wa.setBackgroundResource(configs.iconWA);
            }
        }
        return vh;
    }

    public void setOnStickerClickListener(StickerClickListener listener) {
        stickerClickListener = listener;
    }

    @Override
    public void setData(List<PackageModel> data) {
        super.setData(data);
        if (data == null) {
            data = new ArrayList<>();
        }
        if (this.originalList == null) { // fill only the first time
            this.originalList = new ArrayList<>(data);
        }
    }

    public void filter(String query) {
        if (this.originalList == null) {
            return;
        }
        if (TextUtils.isEmpty(query)) {
            setData(this.originalList);
            return;
        }
        ArrayList<PackageModel> list = new ArrayList<>();
        for (Iterator<PackageModel> it = this.originalList.iterator(); it.hasNext(); ) {
            PackageModel next = it.next();
            if (next.contains(query)) {
                list.add(next);
            }
        }
        setData(list);
    }

    public interface StickerClickListener {

        void selected(StickerPackContainer model);
    }

    public static class StickerPackContainer {
        public String url;
        public String packageName;

        public StickerPackContainer(String url, String packageName) {
            this.url = url;
            this.packageName = packageName;
        }
    }

    public static class StickerImageAdapter extends BaseAdapter<StickerPackContainer, StickerImageAdapter.ViewHolder> {
        public StickerClickListener stickerClickListener;

        StickerImageAdapter(Activity activity, List<StickerPackContainer> data, StickerClickListener stickerClickListener) {
            super(activity, R.layout.item_sticker_image, data);
            this.stickerClickListener = stickerClickListener;
        }

        @Override
        public ViewHolder createViewHolder(View view) {
            return new ViewHolder(view);
        }

        public class ViewHolder extends BindableViewHolder<StickerPackContainer> {
            ImageView image;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
            }

            @Override
            public void bindTo(final Activity activity, final StickerPackContainer model, int position) {
                Glide.with(itemView.getContext())
                        .load(model.url)
                        .placeholder(Utils.getRandomColoredDrawable())
                        .fitCenter()
                        .override(Utils.DEFAULT_IMG_SIZE, Utils.DEFAULT_IMG_SIZE)
                        .into(image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (stickerClickListener != null) {
                            stickerClickListener.selected(model);
                        }
                    }
                });

            }
        }
    }

    public class StickerPackViewHolder extends BindableViewHolder<PackageModel> implements View.OnClickListener {
        TextView name;
        Button add_to_wa;
        Button download;
        RecyclerView recyclerView;
        StickerImageAdapter stickerImageAdapter;
        private PackageModel model;
        private Activity activity;

        StickerPackViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            add_to_wa = itemView.findViewById(R.id.add_to_wa);
            download = itemView.findViewById(R.id.download);
            recyclerView = itemView.findViewById(R.id.recycler_view);

        }

        @Override
        public void bindTo(final Activity activity, final PackageModel model, int position) {
            this.model = model;
            this.activity = activity;
            name.setText(model.name);

            ArrayList<StickerPackContainer> data = new ArrayList<>();
            for (StickerModel sticker : model.stickers) {
                StickerPackContainer s = new StickerPackContainer(sticker.image, model.name);
                data.add(s);
            }
            stickerImageAdapter = new StickerImageAdapter(activity, data, stickerClickListener);
            LinearLayoutManager sgl = new LinearLayoutManager(itemView.getContext(), StaggeredGridLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(sgl);
            recyclerView.setAdapter(stickerImageAdapter);

            download.setVisibility(View.VISIBLE);
            add_to_wa.setVisibility(!StickerPackViewHolder.this.model.isInWhiteList ? View.VISIBLE : View.GONE);
            add_to_wa.setOnClickListener(this);
            download.setOnClickListener(this);

            Log.e(TAG, "bindTo");
        }

        @Override
        public void onClick(View v) {
            final int id = v.getId();
            permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
                @Override
                public void run() {
                    if (id == R.id.download) {
                        download(activity, model);
                    } else if (id == R.id.add_to_wa) {
                        addToWhatsapp();
                    }
                }
            });

        }

        public boolean checkPermission(Context context, String str) {
            return !(AdmUtils.isContextInvalid(context) || context.checkCallingOrSelfPermission(str) != PackageManager.PERMISSION_GRANTED);
        }

        private void addToWhatsapp() {
            boolean isInWhiteList = WhitelistCheck.isWhitelisted(activity, model.id);
            add_to_wa.setVisibility(!isInWhiteList ? View.VISIBLE : View.GONE);
            if (isInWhiteList) {
                return;
            }

            final AlertDialog loading = new AlertDialog.Builder(activity)
                    .setTitle("Importing to Whatsapp..")
                    .setMessage("Please be patient, it takes few secs.")
                    .setCancelable(false)
                    .create();

            loading.show();

            new Thread() {
                @Override
                public void run() {
                    final StickerPack pack = Utils.toWAStickerPack(activity, model);
                    if (pack == null || AdmUtils.isContextInvalid(activity)) {
                        return;
                    }
                    WAStickerHelper.adjustSaveAndAddPack(activity, pack, new WorkCounter.Listener() {
                        @Override
                        public void completed() {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    add_to_wa.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            }.start();
        }

        private void download(final Activity activity, final PackageModel model) {
            permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void run() {
                    if (AdmUtils.isContextInvalid(activity)) {
                        return;
                    }
                    Toast.makeText(activity, "Downloading: " + model.name, Toast.LENGTH_LONG).show();

                    File folder = new File(Utils.getDownloadFolder(activity) + "/" + model.name);
                    folder.mkdirs();
                    final WorkCounter workCounter = new WorkCounter(model.stickers.size(), new WorkCounter.Listener() {
                        @Override
                        public void completed() {
                            if (AdmUtils.isContextInvalid(activity)) {
                                return;
                            }
                            Toast.makeText(activity, "Downloaded to gallery: " + model.name, Toast.LENGTH_LONG).show();
                        }
                    });

                    List<StickerModel> stickers = model.stickers;
                    for (int i = 0; i < stickers.size(); i++) {
                        final StickerModel stickerModel = stickers.get(i);

                        final File file = new File(folder.getAbsolutePath() + "/" + model.name + "_" + (i + 1) + ".png");
                        if (file.exists()) file.delete();

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {

                                WAStickerHelper.downloadStickerImage(activity, stickerModel.image, new WAStickerHelper.DownloadListener() {
                                    @Override
                                    public void downloaded(File resource) {

                                        try {
                                            Utils.copyFile(resource, file);

                                            if (!AdmUtils.isContextInvalid(activity)) {
                                                Utils.addToGallery(activity, file);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        workCounter.taskFinished();
                                    }
                                });
                                return null;
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                }
            });
        }
    }
}