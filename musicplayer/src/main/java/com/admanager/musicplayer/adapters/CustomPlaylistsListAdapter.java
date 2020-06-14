package com.admanager.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.listeners.MPCustomItemClickListener;
import com.admanager.musicplayer.models.Playlist;

import java.util.ArrayList;
import java.util.List;

public class CustomPlaylistsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CONTENT_TYPE = 0;
    private final MPCustomItemClickListener listener;
    private List<Playlist> rowItems;

    public CustomPlaylistsListAdapter(final List<Playlist> rowPlaylistItems, MPCustomItemClickListener listener) {
        this.rowItems = new ArrayList<>(rowPlaylistItems);
        this.listener = listener;
    }

    public void clear() {
        rowItems.clear();
        notifyDataSetChanged();
    }

    public void setData(List<Playlist> data) {
        rowItems = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public void update(int index, Playlist rowItem) {
        rowItems.set(index, rowItem);
        notifyItemChanged(index);
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    public Playlist getItem(int index) {
        return rowItems.get(index);
    }

    @Override
    public int getItemViewType(int position) {
        return CONTENT_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int viewType) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mp_list_playlists, viewGroup, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Playlist playlist = getItem(position);

        ((ViewHolder) viewHolder).txtPlaylist.setText(playlist.getName());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtPlaylist;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            View mlPlaylistImageLayout = itemLayoutView.findViewById(R.id.mlPlaylistImageLayout);
            View mlPlaylistLayout = itemLayoutView.findViewById(R.id.mlPlaylistLayout);
            View mlOptionLayout = itemLayoutView.findViewById(R.id.mlOptionLayout);

            txtPlaylist = itemLayoutView.findViewById(R.id.mlPlaylist);

            View.OnClickListener onClickListener = view -> listener.onItemClick(view, getAdapterPosition());

            mlPlaylistImageLayout.setOnClickListener(onClickListener);
            mlPlaylistLayout.setOnClickListener(onClickListener);
            mlOptionLayout.setOnClickListener(onClickListener);
        }
    }
}
