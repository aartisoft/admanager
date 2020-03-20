package com.admanager.wastickers.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.admanager.recyclerview.AdmAdapterConfiguration;
import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;
import com.admanager.wastickers.R;
import com.admanager.wastickers.model.CategoryModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class StickerMainCategoryAdapter extends BaseAdapter<CategoryModel, StickerMainCategoryAdapter.CategoryViewHolder> {
    public static final String TAG = "GifCategoryAdapter";
    private CategoryClickListener categoryClickListener;

    public StickerMainCategoryAdapter(Activity activity) {
        super(activity, R.layout.item_sticker_category);
    }

    @Override
    protected AdmAdapterConfiguration<?> configure() {
        return new AdmAdapterConfiguration<>()
                .density(99);
    }

    @Override
    public CategoryViewHolder createViewHolder(View view) {
        return new CategoryViewHolder(view);
    }

    public void setOnCategoryClickListener(CategoryClickListener listener) {
        categoryClickListener = listener;
    }

    @Override
    public void setData(List<CategoryModel> data) {
        super.setData(data);
    }

    public interface CategoryClickListener {
        void selected(CategoryModel model);
    }

    public class CategoryViewHolder extends BindableViewHolder<CategoryModel> {
        TextView name;
        ImageView image;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }

        @Override
        public void bindTo(final Activity activity, final CategoryModel model, int position) {
            name.setText(model.name);
            Glide.with(itemView.getContext())
                    .load(model.image)
                    .into(image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (categoryClickListener != null) {
                        categoryClickListener.selected(model);
                    }
                }
            });
        }
    }
}