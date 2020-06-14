package com.admanager.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.listeners.MPCustomItemClickListener;
import com.admanager.musicplayer.models.Genre;

import java.util.ArrayList;
import java.util.List;

public class CustomGenresListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CONTENT_TYPE = 0;
    private final Context context;
    private final MPCustomItemClickListener listener;
    private List<Genre> rowItems;

    public CustomGenresListAdapter(Context context, final List<Genre> rowGenreItems, MPCustomItemClickListener listener) {
        this.context = context;
        this.rowItems = new ArrayList<>(rowGenreItems);
        this.listener = listener;
    }

    public void clear() {
        rowItems.clear();
        notifyDataSetChanged();
    }

    public void setData(List<Genre> data) {
        rowItems = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public void update(int index, Genre rowItem) {
        rowItems.set(index, rowItem);
        notifyItemChanged(index);
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    public Genre getItem(int index) {
        return rowItems.get(index);
    }

    @Override
    public int getItemViewType(int position) {
        return CONTENT_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int viewType) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mp_list_genres, viewGroup, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Genre genre = getItem(position);

        ((ViewHolder) viewHolder).txtGenre.setText(genre.getName());

        List<Long> audioIds = genre.getAudioIds();
        int totalTrackNo = 0;
        if (audioIds != null) {
            totalTrackNo = audioIds.size();
        }

        if (context != null) {
            ((ViewHolder) viewHolder).txtTotalTrack.setText(context.getResources().getQuantityString(R.plurals.mp_number_tracks, totalTrackNo, totalTrackNo));
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtGenre;
        private final TextView txtTotalTrack;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            View mlGenreImageLayout = itemLayoutView.findViewById(R.id.mlGenreImageLayout);
            View mlGenreLayout = itemLayoutView.findViewById(R.id.mlGenreLayout);
            View mlTotalTrackLayout = itemLayoutView.findViewById(R.id.mlTotalTrackLayout);
            View mlOptionLayout = itemLayoutView.findViewById(R.id.mlOptionLayout);

            txtGenre = itemLayoutView.findViewById(R.id.mlGenre);
            txtTotalTrack = itemLayoutView.findViewById(R.id.mlTotalTrack);

            View.OnClickListener onClickListener = view -> listener.onItemClick(view, getAdapterPosition());

            mlGenreImageLayout.setOnClickListener(onClickListener);
            mlGenreLayout.setOnClickListener(onClickListener);
            mlTotalTrackLayout.setOnClickListener(onClickListener);
            mlOptionLayout.setOnClickListener(onClickListener);
        }
    }
}
