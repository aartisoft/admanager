package com.admanager.applocker.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admanager.applocker.R;
import com.admanager.applocker.adapter.ApplicationListAdapter;
import com.admanager.applocker.adapter.GetListOfAppsAsyncTask;
import com.admanager.applocker.data.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AllAppFragment extends BaseFragment {

    private static final String TYPE = "type";
    List<AppInfo> list;
    private RecyclerView mRecyclerView;
    private View loadingLayout;
    private RecyclerView.Adapter mAdapter;
    private String requiredAppsType;

    public AllAppFragment() {
        super();
    }

    public static AllAppFragment newInstance(String requiredApps) {
        AllAppFragment f = new AllAppFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, requiredApps);
        f.setArguments(args);
        return (f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_all_apps, container, false);

        requiredAppsType = getArguments().getString(TYPE);

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        loadingLayout = v.findViewById(R.id.loading_layout);
        mRecyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ApplicationListAdapter(list, getMainActivity());
        mRecyclerView.setAdapter(mAdapter);

        GetListOfAppsAsyncTask task = new GetListOfAppsAsyncTask(this);
        task.execute(requiredAppsType);

        return v;

    }

    public void showProgressBar() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        loadingLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void updateData(List<AppInfo> list) {
        this.list.clear();
        this.list.addAll(list);
        mAdapter.notifyDataSetChanged();
    }
}
