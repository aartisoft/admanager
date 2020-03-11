package com.admanager.colorcallscreen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.admanager.colorcallscreen.ColorCallScreenApp;
import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.activities.ColorCallScreenActivity;
import com.admanager.colorcallscreen.adapter.CallScreenViewHolder;
import com.admanager.colorcallscreen.adapter.CategoryAdapter;
import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.colorcallscreen.api.CallScreenService;
import com.admanager.colorcallscreen.api.CategoryModel;
import com.admanager.colorcallscreen.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CategoryFragment extends BaseFragment {
    public static final String TAG = "CategoryFragment";
    public CategoryAdapter adapter;
    Disposable subscribe;
    RecyclerView recyclerView;

    public static void importImageClicked(Activity act) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        act.startActivityForResult(Intent.createChooser(intent, act.getString(R.string.picture_select)), ColorCallScreenActivity.REQUEST_GET_SINGLE_FILE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ccs_fragment_category, parent, false);
        getColorCallScreenActivity().setTitle(getString(R.string.colorCallScreen));
        recyclerView = view.findViewById(R.id.recycler_view);
        setAdapter();

        return view;
    }

    private void setAdapter() {
        adapter = new CategoryAdapter(getColorCallScreenActivity());
        ArrayList<CategoryModel> data = new ArrayList<>();
        data.add(null); // import button
        adapter.setData(data);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        loadImages();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
            subscribe = null;
        }
    }

    private void loadImages() {
        adapter.loadingMore();
        subscribe = CallScreenService.api(getContext()).getCategories()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CategoryModel>>() {
                    @Override
                    public void accept(List<CategoryModel> list) throws Exception {
                        list.add(0, null);
                        adapter.setData(list);
                        adapter.loaded();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        adapter.loaded();
                    }
                });

    }

    public void imagePicked(Uri selectedImageUri) {
        BgModel bg = BgModel.randomImportedName();

        File fileDir = Utils.getFilesFolder(getContext(), bg);
        InputStream inputStream = null;
        try {
            inputStream = getColorCallScreenActivity().getContentResolver().openInputStream(selectedImageUri);

            OutputStream output = null;
            try {
                output = new FileOutputStream(fileDir);
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;

                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }

                output.close();
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    output.close();
                }
            }
            if (inputStream != null) {
                inputStream.close();

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        bg.image = fileDir.getAbsolutePath();

        int i = CallScreenViewHolder.randomIndex();
        String name = CallScreenViewHolder.randomName(i);
        Uri uri = CallScreenViewHolder.randomUri(getColorCallScreenActivity(), i);

        Bundle bundle = BgDetailsFragment.createBgDetailsBundle(name, uri, bg, ColorCallScreenApp.NUMBER, true);
        getNavController().navigate(R.id.bgDetailsFragment, bundle);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getColorCallScreenActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}