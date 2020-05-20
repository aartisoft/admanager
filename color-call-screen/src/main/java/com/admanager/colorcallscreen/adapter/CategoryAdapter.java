package com.admanager.colorcallscreen.adapter;

import android.app.Activity;
import android.view.View;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.api.CategoryModel;
import com.admanager.recyclerview.AdmAdapterConfiguration;
import com.admanager.recyclerview.BaseAdapter;

public class CategoryAdapter extends BaseAdapter<CategoryModel, CategoryViewHolder> {

    public CategoryAdapter(Activity activity) {
        super(activity, R.layout.adm_ccs_item_category);
    }

    @Override
    protected AdmAdapterConfiguration<?> configure() {
        return new AdmAdapterConfiguration<>()
                .gridSize(2)
                .density(99);
    }

    @Override
    protected CategoryViewHolder createViewHolder(View view) {
        return new CategoryViewHolder(view);
    }

}
