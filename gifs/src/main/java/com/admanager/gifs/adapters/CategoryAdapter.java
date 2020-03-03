package com.admanager.gifs.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.admanager.gifs.R;
import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;
import com.giphy.sdk.core.models.Category;

public class CategoryAdapter extends BaseAdapter<Category, CategoryAdapter.ViewHolder> {
    private final SelectedListener selectedListener;
    private String selected = null;

    public CategoryAdapter(Activity activity, SelectedListener selectedListener) {
        super(activity, R.layout.item_gif_category);
        this.selectedListener = selectedListener;
    }

    public void setSelected(String selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public interface SelectedListener {
        void selected(Category category);
    }

    public class ViewHolder extends BindableViewHolder<Category> {
        TextView title;
        View root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            root = itemView.findViewById(R.id.root);
        }

        @Override
        public void bindTo(Activity activity, final Category model, int position) {
            title.setText(model.getName());
            boolean selectedBoolean = selected != null && selected.equals(model.getName());
            root.setSelected(selectedBoolean);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedListener.selected(model);
                }
            });
        }
    }
}
