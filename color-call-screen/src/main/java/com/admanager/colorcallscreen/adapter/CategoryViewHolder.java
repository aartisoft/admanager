package com.admanager.colorcallscreen.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.activities.ColorCallScreenActivity;
import com.admanager.colorcallscreen.api.CategoryModel;
import com.admanager.recyclerview.BindableViewHolder;
import com.bumptech.glide.Glide;

public class CategoryViewHolder extends BindableViewHolder<CategoryModel> {
    private ImageView image;
    private TextView title;
    private View root;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        image = itemView.findViewById(R.id.image);
        root = itemView.findViewById(R.id.root);
    }

    @Override
    public void bindTo(final Activity activity, final CategoryModel model, int position) {
        image.setVisibility(View.VISIBLE);
        title.setText(model.name);

        Glide.with(activity)
                .load(model.image)
                .into(image);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof ColorCallScreenActivity) {
                    Bundle args = new Bundle();
                    args.putString("category", model.name);
                    ((ColorCallScreenActivity) activity).navController.navigate(R.id.categoryDetailsFragment, args);
                }
            }
        });
    }
}
