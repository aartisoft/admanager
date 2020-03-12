package com.admanager.unseen.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admanager.unseen.R;
import com.admanager.unseen.notiservice.models.Message;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by a on 29.03.2017.
 */

public class MessagesAdapter extends RealmRecyclerViewAdapter<Message, RecyclerView.ViewHolder> {
    public MessagesAdapter(@Nullable OrderedRealmCollection<Message> data, boolean autoUpdate) {
        super(data, autoUpdate);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_messages, null);
        holder = new MessagesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).bindTo();
        } else if (holder instanceof MessagesViewHolder) {
            int pos = getRealPosition(position);

            String prevUserName = null;
            if (position > 0) {
                int prevPos = getRealPosition(position - 1);
                prevUserName = getItem(prevPos).getUser().getName();
            }
            ((MessagesViewHolder) holder).bindTo(getItem(pos), prevUserName);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        int i = super.getItemCount();
        return i;

    }

    private int getRealPosition(int pos) {
        return pos;
    }


}
