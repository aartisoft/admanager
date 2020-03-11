package com.admanager.colorcallscreen.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.model.ContactBean;
import com.admanager.colorcallscreen.utils.Prefs;
import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;

import java.util.List;

public class ContactListAdapter extends BaseAdapter<ContactBean, ContactListAdapter.ContactListViewHolder> {

    public ContactListAdapter(Activity activity) {
        super(activity, R.layout.ccs_item_contact_list);
    }

    @Override
    protected ContactListViewHolder createViewHolder(View view) {
        return new ContactListViewHolder(view);
    }

    public class ContactListViewHolder extends BindableViewHolder<ContactBean> {
        ImageView delete;
        TextView name;

        public ContactListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            delete = itemView.findViewById(R.id.delete);
        }

        @Override
        public void bindTo(final Activity activity, final ContactBean model, int position) {
            name.setText(model.displayName);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Prefs.with(activity).removeSelectedBgForUser(model);

                    List<ContactBean> data = getData();
                    data.remove(model);
                    setData(data);
                }
            });
        }
    }
}
