package com.admanager.unseen.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.unseen.R;
import com.admanager.unseen.notiservice.models.Conversation;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by a on 29.03.2017.
 */

public class ConversationsAdapter extends RealmRecyclerViewAdapter<Conversation, RecyclerView.ViewHolder> {
    private final AppCompatActivity activity;

    public ConversationsAdapter(AppCompatActivity act, @Nullable OrderedRealmCollection<Conversation> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.activity = act;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_conversation, null);
        holder = new ConversationViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).bindTo();
        } else if (holder instanceof ConversationViewHolder) {
            int pos = getRealPosition(position);
            ((ConversationViewHolder) holder).bindTo(activity, getItem(pos));
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

    // todo empty ise Data yok diye uyari gostersek
}
