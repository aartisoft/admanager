package com.admanager.colorcallscreen.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.adapter.CallScreenAdapter;
import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.colorcallscreen.api.CallScreenService;
import com.admanager.colorcallscreen.utils.EndlessRecyclerViewScrollListener;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CategoryDetailsFragment extends BaseFragment {
    public static final String TAG = "CategoryFragment";
    public CallScreenAdapter adapter;
    String category;
    Disposable subscribe;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ccs_fragment_category_detail, parent, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        category = (String) getArguments().get("category");
        getColorCallScreenActivity().setTitle(category);

        adapter = new CallScreenAdapter(getColorCallScreenActivity());
        GridLayoutManager layout = new GridLayoutManager(getContext(), adapter.getGridSize());
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                loadImages(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        loadImages(1);

        return view;
    }

    private void loadImages(int page) {
        Log.e(TAG, "PAGE: " + page);
        adapter.loadingMore();
        subscribe = CallScreenService.api(getContext()).getBgs(page - 1, category)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<BgModel>>() {
                    @Override
                    public void accept(List<BgModel> bgModels) throws Exception {
                        List<BgModel> data = adapter.getData();
                        data.addAll(bgModels);
                        adapter.setData(data);
                        adapter.loaded();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        adapter.loaded();
                    }
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
            subscribe = null;
        }
    }

}