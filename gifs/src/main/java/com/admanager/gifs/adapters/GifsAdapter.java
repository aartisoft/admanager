package com.admanager.gifs.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.admanager.gifs.R;
import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;
import com.bumptech.glide.Glide;
import com.giphy.sdk.core.models.Media;

public class GifsAdapter extends BaseAdapter<Media, GifsAdapter.ViewHolder> {
    private final GifsAdapter.SelectedListener selectedListener;

    public GifsAdapter(Activity activity, GifsAdapter.SelectedListener selectedListener) {
        super(activity, R.layout.item_gif);
        this.selectedListener = selectedListener;
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public interface SelectedListener {
        void selected(Media media);
    }

    public class ViewHolder extends BindableViewHolder<Media> {
        TextView title;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
        }

        @Override
        public void bindTo(Activity activity, final Media model, int position) {
            title.setText(model.getTitle());
            Glide.with(itemView.getContext())
                    .load(model.getImages().getFixedHeightDownsampled().getGifUrl())
                    .into(img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedListener.selected(model);
                }
            });
        }
    }
}
