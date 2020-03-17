package com.admanager.colorcallscreen.adapter;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.activities.ColorCallScreenActivity;
import com.admanager.colorcallscreen.api.CategoryModel;
import com.admanager.colorcallscreen.fragment.CategoryFragment;
import com.admanager.core.AdmUtils;
import com.admanager.recyclerview.BindableViewHolder;
import com.bumptech.glide.Glide;

public class CategoryViewHolder extends BindableViewHolder<CategoryModel> {
    int RAD = 20;
    int colors[] = new int[]{
            R.color.colorCategory1,
            R.color.colorCategory2,
            R.color.colorCategory3,
            R.color.colorCategory4,
            R.color.colorCategory5,
            R.color.colorCategory6,
            R.color.colorCategory7,
            R.color.colorCategory8};
    private ImageView image;
    private TextView title;
    private LinearLayout root;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        image = itemView.findViewById(R.id.image);
        root = itemView.findViewById(R.id.root);
    }

    @Override
    public void bindTo(final Activity activity, final CategoryModel model, int position) {
        if (model == null) {
            root.setBackgroundResource(R.drawable.btn_importimage);

            title.setText(R.string.import_text);
            image.setVisibility(View.GONE);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryFragment.importImageClicked(activity);
                }
            });
            return;
        }
        image.setVisibility(View.VISIBLE);
        title.setText(model.name);

        int w = (int) AdmUtils.dpToPx(activity, 48);
        Glide.with(activity)
                .load(model.icon)
                .override(w, w)
                .into(image);

        int draw = colors[position % colors.length];
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadii(new float[]{RAD, RAD, RAD, RAD, RAD, RAD, RAD, RAD});
        gradientDrawable.setColor(ContextCompat.getColor(activity, draw));
        root.setBackground(gradientDrawable);

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
