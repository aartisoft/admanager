package com.admanager.unseen.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.admanager.unseen.R;
import com.admanager.unseen.notiservice.models.Message;
import com.admanager.unseen.utils.Utils;
import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by a on 29.03.2017.
 */
public class MessagesViewHolder extends RecyclerView.ViewHolder {
    public TextView userName, time, message;
    private ColorGenerator generator;

    public MessagesViewHolder(View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.userName);
        message = itemView.findViewById(R.id.message);
        time = itemView.findViewById(R.id.time);

        generator = ColorGenerator.MATERIAL;
    }

    public void bindTo(final Message c, String prevUserName) {
        String uname = c.getUser().getName();
        if (prevUserName != null && uname.equals(prevUserName)) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
        }
        userName.setText(uname);
        message.setText(c.getMessage());
        time.setText(Utils.getStringFormattedDate(itemView.getContext(), c.getTime()));

        userName.setTextColor(generator.getColor(uname));
    }

}
