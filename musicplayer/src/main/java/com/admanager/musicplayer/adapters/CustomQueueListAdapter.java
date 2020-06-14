package com.admanager.musicplayer.adapters;

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
import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

public class CustomQueueListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CONTENT_TYPE = 0;
    private final MPCustomItemClickListener listener;
    private List<Track> rowItems;
    private int lastPlayingTrack = 0;

    public CustomQueueListAdapter(final List<Track> rowTrackItems, MPCustomItemClickListener listener) {
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

    public void setData(List<Track> data, int lastPlayingTrackPosition) {
        rowItems = new ArrayList<>(data);
        lastPlayingTrack = lastPlayingTrackPosition;
        notifyDataSetChanged();
    }

    public void update(int index, Track rowItem) {
        rowItems.set(index, rowItem);
        notifyItemChanged(index);
    }

    public void insert(int index, Track track) {
        rowItems.add(index, track);
        notifyItemInserted(index);
    }

    public void add(Track track) {
        rowItems.add(track);
        notifyItemInserted(rowItems.size() - 1);
    }

    public void remove(int position) {
        rowItems.remove(position);
        //notifyItemRemoved(position);
        //position number must be updated. So notifyDataSetChanged is used.
        notifyDataSetChanged();
    }

    public void moveToFirst(int position) {
        if (position < 1) return;

        Track track = getItem(position);
        if (track == null) return;

        if (getItemCount() <= 1) return;

        if (lastPlayingTrack == position) {
            lastPlayingTrack = 0;
        }

        rowItems.remove(position);
        rowItems.add(0, track);

        notifyDataSetChanged();
    }

    public void moveToLast(int position) {
        if (position < 0) return;

        Track track = getItem(position);
        if (track == null) return;

        if (getItemCount() <= 1) return;
        if ((position + 1) == getItemCount()) return;

        if (lastPlayingTrack == position) {
            lastPlayingTrack = getItemCount() - 1;
        }

        rowItems.remove(position);
        rowItems.add(track);

        notifyDataSetChanged();
    }

    public void moveUp(int position) {
        if (position < 1) return;

        Track track = getItem(position);
        if (track == null) return;

        if (getItemCount() <= 1) return;

        if (lastPlayingTrack == position) {
            lastPlayingTrack = position - 1;
        }

        Track previousTrack = getItem(position - 1);

        update(position - 1, track);
        update(position, previousTrack);

        notifyDataSetChanged();
    }

    public void moveDown(int position) {
        if (position < 0) return;

        Track track = getItem(position);
        if (track == null) return;

        if (getItemCount() <= 1) return;
        if ((position + 1) == getItemCount()) return;

        if (lastPlayingTrack == position) {
            lastPlayingTrack = position + 1;
        }

        Track nextTrack = getItem(position + 1);

        update(position, nextTrack);
        update(position + 1, track);

        notifyDataSetChanged();
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
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mp_list_queue, viewGroup, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Track track = getItem(position);

        ((ViewHolder) viewHolder).txtTrackNo.setText("" + (position + 1));
        ((ViewHolder) viewHolder).txtTitle.setText(track.getTitle());

        LottieAnimationView animationView = ((ViewHolder) viewHolder).animationView;
        ImageView imageView = ((ViewHolder) viewHolder).imageView;

        if (track.isPlaying()) {
            animationView.setVisibility(View.VISIBLE);
            animationView.setImageAssetsFolder("images/");
            animationView.setAnimation("audio_visualizer.json");
            //lottieAnimationView.loop(false);
            animationView.playAnimation();

            imageView.setVisibility(View.GONE);

            lastPlayingTrack = position;
        } else if (position == lastPlayingTrack) {
            if (!existPlayingTrack()) {
                animationView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.mp_animation);
            } else {
                animationView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.mp_ic_music);
            }
        } else {
            animationView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.mp_ic_music);
        }

        ((ViewHolder) viewHolder).txtTotalMsec.setText(track.getTotalMsec());
    }

    private boolean existPlayingTrack() {
        for (Track track : rowItems) {
            if (track.isPlaying()) return true;
        }

        return false;
    }

    public int getPlayingTrack() {
        for (int i = 0; i < rowItems.size(); i++) {
            Track track = rowItems.get(i);
            if (track.isPlaying()) return i;
        }

        return -1;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTrackNo;
        private final LottieAnimationView animationView;
        private final ImageView imageView;
        private final TextView txtTitle;
        private final TextView txtTotalMsec;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            View mlTrackNoLayout = itemLayoutView.findViewById(R.id.mlTrackNoLayout);
            View mlTrackImageLayout = itemLayoutView.findViewById(R.id.mlTrackImageLayout);
            View mlTitleLayout = itemLayoutView.findViewById(R.id.mlTitleLayout);
            View mlTotalMsecLayout = itemLayoutView.findViewById(R.id.mlTotalMsecLayout);
            View mlOptionLayout = itemLayoutView.findViewById(R.id.mlOptionLayout);

            txtTrackNo = itemLayoutView.findViewById(R.id.mlTrackNo);
            animationView = itemLayoutView.findViewById(R.id.mlAnimationView);
            imageView = itemLayoutView.findViewById(R.id.mlTrackImage);
            txtTitle = itemLayoutView.findViewById(R.id.mlTitle);
            txtTotalMsec = itemLayoutView.findViewById(R.id.mlTotalMsec);

            View.OnClickListener onClickListener = view -> listener.onItemClick(view, getAdapterPosition());

            mlTrackNoLayout.setOnClickListener(onClickListener);
            mlTrackImageLayout.setOnClickListener(onClickListener);
            mlTitleLayout.setOnClickListener(onClickListener);
            mlTotalMsecLayout.setOnClickListener(onClickListener);
            mlOptionLayout.setOnClickListener(onClickListener);
        }
    }
}
