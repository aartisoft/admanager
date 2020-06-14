package com.admanager.musicplayer.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.listeners.MPCustomItemClickListener;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.GetTracks;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class CustomTracksListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CONTENT_TYPE = 0;
    private final Context context;
    private final MPCustomItemClickListener listener;
    private List<Track> rowItems;

    public CustomTracksListAdapter(Context context, final List<Track> rowTrackItems, MPCustomItemClickListener listener) {
        this.context = context;
        this.rowItems = new ArrayList<>(rowTrackItems);
        this.listener = listener;
    }

    public void clear() {
        rowItems.clear();
        notifyDataSetChanged();
    }

    public void setData(List<Track> data) {
        rowItems = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public void update(int index, Track rowItem) {
        rowItems.set(index, rowItem);
        notifyItemChanged(index);
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    public Track getItem(int index) {
        return rowItems.get(index);
    }

    @Override
    public int getItemViewType(int position) {
        return CONTENT_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int viewType) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mp_list_tracks, viewGroup, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Track track = getItem(position);

        ImageView imageView_1 = ((ViewHolder) viewHolder).mlTrackImage;

        String imagePath = GetTracks.getAlbumImagePath(context, track.getAlbumId());
        if (!TextUtils.isEmpty(imagePath)) {
            RequestOptions options = new RequestOptions();
            options = options.diskCacheStrategy(DiskCacheStrategy.ALL);
            options = options.centerCrop();
            options = options.dontAnimate();

            Glide.with(context)
                    .load(imagePath)
                    .apply(options)
                    .into(imageView_1);
        } else {
            imageView_1.setImageResource(R.drawable.mp_ic_music);
        }

        ((ViewHolder) viewHolder).txtTitle.setText(track.getTitle());
        ((ViewHolder) viewHolder).txtArtist.setText(track.getArtist());

        ((ViewHolder) viewHolder).txtTotalMsec.setText(track.getTotalMsec());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mlTrackImage;
        private final TextView txtTitle;
        private final TextView txtArtist;
        private final TextView txtTotalMsec;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            View mlTrackImageLayout = itemLayoutView.findViewById(R.id.mlTrackImageLayout);
            View mlTitleLayout = itemLayoutView.findViewById(R.id.mlTitleLayout);
            View mlTotalMsecLayout = itemLayoutView.findViewById(R.id.mlTotalMsecLayout);
            View mlOptionLayout = itemLayoutView.findViewById(R.id.mlOptionLayout);

            mlTrackImage = itemLayoutView.findViewById(R.id.mlTrackImage);
            txtTitle = itemLayoutView.findViewById(R.id.mlTitle);
            txtArtist = itemLayoutView.findViewById(R.id.mlArtist);
            txtTotalMsec = itemLayoutView.findViewById(R.id.mlTotalMsec);

            View.OnClickListener onClickListener = view -> listener.onItemClick(view, getAdapterPosition());

            mlTrackImageLayout.setOnClickListener(onClickListener);
            mlTitleLayout.setOnClickListener(onClickListener);
            mlTotalMsecLayout.setOnClickListener(onClickListener);
            mlOptionLayout.setOnClickListener(onClickListener);
        }
    }
}
