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

public class CustomAlbumsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CONTENT_TYPE = 0;
    private final Context context;
    private final MPCustomItemClickListener listener;
    private List<Track> rowItems;

    public CustomAlbumsListAdapter(Context context, final List<Track> rowTrackItems, MPCustomItemClickListener listener) {
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
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mp_list_albums, viewGroup, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Track track = getItem(position);

        if (track == null) return;

        ImageView imageView = ((ViewHolder) viewHolder).imageView;

        String imagePath = GetTracks.getAlbumImagePath(context, track.getAlbumId());
        if (!TextUtils.isEmpty(imagePath)) {
            RequestOptions options = new RequestOptions();
            options = options.diskCacheStrategy(DiskCacheStrategy.ALL);
            options = options.centerCrop();
            options = options.dontAnimate();

            Glide.with(context)
                    .load(imagePath)
                    .apply(options)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.mp_ic_music);
        }

        ((ViewHolder) viewHolder).txtAlbum.setText(track.getAlbum());
        ((ViewHolder) viewHolder).txtArtist.setText(track.getArtist());

        List<Track> trackList = GetTracks.getAllTracksForAlbum(context, track.getAlbumId());
        int totalTrackNo = 0;
        if (trackList != null) {
            totalTrackNo = trackList.size();
        }

        if (context != null) {
            ((ViewHolder) viewHolder).txtTotalTrack.setText(context.getResources().getQuantityString(R.plurals.mp_number_tracks, totalTrackNo, totalTrackNo));
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView txtAlbum;
        private final TextView txtArtist;
        private final TextView txtTotalTrack;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            View mlTrackImageLayout = itemLayoutView.findViewById(R.id.mlTrackImageLayout);
            View mlAlbumLayout = itemLayoutView.findViewById(R.id.mlAlbumLayout);
            View mlTotalTrackLayout = itemLayoutView.findViewById(R.id.mlTotalTrackLayout);
            View mlOptionLayout = itemLayoutView.findViewById(R.id.mlOptionLayout);

            imageView = itemLayoutView.findViewById(R.id.mlTrackImage);
            txtAlbum = itemLayoutView.findViewById(R.id.mlAlbum);
            txtArtist = itemLayoutView.findViewById(R.id.mlArtist);
            txtTotalTrack = itemLayoutView.findViewById(R.id.mlTotalTrack);

            View.OnClickListener onClickListener = view -> listener.onItemClick(view, getAdapterPosition());

            mlTrackImageLayout.setOnClickListener(onClickListener);
            mlAlbumLayout.setOnClickListener(onClickListener);
            mlTotalTrackLayout.setOnClickListener(onClickListener);
            mlOptionLayout.setOnClickListener(onClickListener);
        }
    }
}
