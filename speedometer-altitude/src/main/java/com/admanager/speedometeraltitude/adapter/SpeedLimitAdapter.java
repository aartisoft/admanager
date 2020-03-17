package com.admanager.speedometeraltitude.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;
import com.admanager.speedometeraltitude.R;
import com.admanager.speedometeraltitude.model.SpeedLimit;

public class SpeedLimitAdapter extends BaseAdapter<SpeedLimit, SpeedLimitAdapter.ViewHolder> {

    private final SelectedListener selectedListener;
    private int id = -1;

    public SpeedLimitAdapter(Activity activity, SelectedListener selectedListener) {
        super(activity, R.layout.item_speed);
        this.selectedListener = selectedListener;
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public void setSelected(int id) {
        this.id = id;
        notifyDataSetChanged();
    }

    public interface SelectedListener {
        void selectedItem(SpeedLimit speedLimit);
    }

    public class ViewHolder extends BindableViewHolder<SpeedLimit> {

        TextView itemText;
        View root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.item_text);
            root = itemView.findViewById(R.id.mRoot);
        }

        @Override
        public void bindTo(Activity activity, final SpeedLimit model, int position) {
            itemText.setText(model.getTitle());
            boolean selectedBoolean = id != -1 && id == model.getId();
            root.setSelected(selectedBoolean);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedListener.selectedItem(model);
                }
            });
        }
    }
}
