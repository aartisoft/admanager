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
import com.admanager.musicplayer.models.FileObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomFolderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CONTENT_TYPE = 0;
    private final MPCustomItemClickListener listener;
    private List<FileObject> rowItems;

    public CustomFolderListAdapter(final List<FileObject> rowFolderItems, MPCustomItemClickListener listener) {
        this.rowItems = new ArrayList<>(rowFolderItems);
        this.listener = listener;
    }

    public void setData(List<FileObject> data) {
        rowItems = new ArrayList<>(data);

        notifyDataSetChanged();
    }

    public void update(int index, FileObject rowItem) {
        rowItems.set(index, rowItem);
        notifyItemChanged(index);
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    public FileObject getItem(int index) {
        return rowItems.get(index);
    }

    @Override
    public int getItemViewType(int position) {
        return CONTENT_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int viewType) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mp_list_folders, viewGroup, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        FileObject fileObject = getItem(position);

        File file = new File(fileObject.getFilePath());
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                ((ViewHolder) viewHolder).imageFolder.setImageResource(R.drawable.mp_ic_folder);
            } else {
                ((ViewHolder) viewHolder).imageFolder.setImageResource(R.drawable.mp_ic_music);
            }
        }

        ((ViewHolder) viewHolder).txtFolder.setText(fileObject.getName());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageFolder;
        private final TextView txtFolder;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            View mlFolderImageLayout = itemLayoutView.findViewById(R.id.mlFolderImageLayout);
            View mlFolderLayout = itemLayoutView.findViewById(R.id.mlFolderLayout);
            View mlOptionLayout = itemLayoutView.findViewById(R.id.mlOptionLayout);

            imageFolder = itemLayoutView.findViewById(R.id.mlFolderImage);
            txtFolder = itemLayoutView.findViewById(R.id.mlFolder);

            View.OnClickListener onClickListener = view -> listener.onItemClick(view, getAdapterPosition());

            mlFolderImageLayout.setOnClickListener(onClickListener);
            mlFolderLayout.setOnClickListener(onClickListener);
            mlOptionLayout.setOnClickListener(onClickListener);
        }
    }
}
