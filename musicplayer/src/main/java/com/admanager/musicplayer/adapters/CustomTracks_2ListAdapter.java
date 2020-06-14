package com.admanager.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.listeners.MPCustomItemClickListener;
import com.admanager.musicplayer.models.Track;

import java.util.ArrayList;
import java.util.List;

public class CustomTracks_2ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CONTENT_TYPE = 0;
    private final MPCustomItemClickListener listener;
    private List<Track> rowItems;

    public CustomTracks_2ListAdapter(final List<Track> rowTrackItems, MPCustomItemClickListener listener) {
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

    public void remove(int index) {
        rowItems.remove(index);
        notifyItemRemoved(index);
    }

    public void insert(int index, Track track) {
        rowItems.add(index, track);
        notifyDataSetChanged();
    }

    public void add(Track track) {
        rowItems.add(track);
        notifyItemInserted(rowItems.size() - 1);
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
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mp_list_tracks_2, viewGroup, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Track track = getItem(position);

        ((ViewHolder) viewHolder).txtTrackNo.setText("" + (position + 1));
        ((ViewHolder) viewHolder).txtTitle.setText(track.getTitle());

        ((ViewHolder) viewHolder).txtTotalMsec.setText(track.getTotalMsec());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTrackNo;
        private final TextView txtTitle;
        private final TextView txtTotalMsec;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            View mlTrackNoLayout = itemLayoutView.findViewById(R.id.mlTrackNoLayout);
            View mlTitleLayout = itemLayoutView.findViewById(R.id.mlTitleLayout);
            View mlTotalMsecLayout = itemLayoutView.findViewById(R.id.mlTotalMsecLayout);
            View mlOptionLayout = itemLayoutView.findViewById(R.id.mlOptionLayout);

            txtTrackNo = itemLayoutView.findViewById(R.id.mlTrackNo);
            txtTitle = itemLayoutView.findViewById(R.id.mlTitle);
            txtTotalMsec = itemLayoutView.findViewById(R.id.mlTotalMsec);

            View.OnClickListener onClickListener = view -> listener.onItemClick(view, getAdapterPosition());

            mlTrackNoLayout.setOnClickListener(onClickListener);
            mlTitleLayout.setOnClickListener(onClickListener);
            mlTotalMsecLayout.setOnClickListener(onClickListener);
            mlOptionLayout.setOnClickListener(onClickListener);
        }
    }
}
