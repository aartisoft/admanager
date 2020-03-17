package com.admanager.unseen.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.unseen.R;
import com.admanager.unseen.activities.DetailActivity;
import com.admanager.unseen.notiservice.models.Conversation;
import com.admanager.unseen.notiservice.models.Message;
import com.admanager.unseen.utils.Utils;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.Locale;

/**
 * Created by a on 29.03.2017.
 */
public class ConversationViewHolder extends RecyclerView.ViewHolder {
    public TextView userName, time, message;
    public ImageView type;
    public TextView typeText;
    public ImageView image;

    ColorGenerator generator; // or use DEFAULT
    private AppCompatActivity activity;

    public ConversationViewHolder(View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.userName);
        message = (TextView) itemView.findViewById(R.id.message);
        typeText = (TextView) itemView.findViewById(R.id.typeText);
        time = (TextView) itemView.findViewById(R.id.time);
        type = (ImageView) itemView.findViewById(R.id.type);
        image = (ImageView) itemView.findViewById(R.id.image);
        generator = ColorGenerator.MATERIAL;
    }

    public void bindTo(final AppCompatActivity activity, final Conversation c) {
        this.activity = activity;
        Message lm = c.getLastMessage();
        String name = c.getTitle();
        userName.setText(Utils.shortenText(name));
        if (lm != null) {
            message.setText(Utils.shortenText(lm.getMessage()));
            time.setText(Utils.getStringFormattedDate(itemView.getContext(), lm.getTime()));
        }

        try {
            Resources resources = itemView.getContext().getResources();
            final int resourceId = resources.getIdentifier(c.getType(), "drawable", itemView.getContext().getPackageName());
            this.type.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), resourceId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        typeText.setText(c.getType().toUpperCase(Locale.ENGLISH));

        if (name == null || name.length() < 1) {
            TextDrawable drawable = TextDrawable.builder().buildRoundRect("??", generator.getColor(name), 50);
            image.setImageDrawable(drawable);
        } else {
            try {
                name = name.trim();
                char lc = name.charAt(name.length() - 1);
                char fc = name.charAt(0);
                if (lc >= 8234 && lc <= 8238) {
                    name = name.substring(0, name.length() - 1);
                }
                if (fc >= 8234 && fc <= 8238) {
                    name = name.substring(1, name.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String first = name.substring(0, 1);
            try {
                int i = Integer.parseInt(first);
                first = "#";
            } catch (NumberFormatException e) {
                if (first.equals("+")) {
                    first = "#";
                }
            }

            TextDrawable drawable = TextDrawable.builder().buildRoundRect(first, generator.getColor(name), 50);
            image.setImageDrawable(drawable);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(itemView.getContext(), DetailActivity.class);
                Bundle b = new Bundle();
                b.putString("conversation", c.getTitle());
                i.putExtras(b);
                itemView.getContext().startActivity(i);
            }
        });
    }

}
