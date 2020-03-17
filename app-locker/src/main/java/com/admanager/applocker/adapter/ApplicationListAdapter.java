package com.admanager.applocker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.applocker.R;
import com.admanager.applocker.animation.Flip3dAnimation;
import com.admanager.applocker.data.AppInfo;
import com.admanager.applocker.prefrence.Prefs;
import com.admanager.applocker.utils.AppLockInitializer;

import java.util.List;
import java.util.Set;

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> {
    private final AppCompatActivity activity;
    private List<AppInfo> installedApps;

    public ApplicationListAdapter(List<AppInfo> appInfoList, AppCompatActivity activity) {
        installedApps = appInfoList;
        this.activity = activity;
    }

    private static void animate(ToggleButton switchView) {
        int i = switchView.isSelected() ? 1 : -1;
        final float centerX = switchView.getWidth() / 2.0f;
        final float centerY = switchView.getHeight() / 2.0f;
        final Flip3dAnimation rotation = new Flip3dAnimation(i * 90, 0, centerX, centerY);
        rotation.setDuration(200);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        switchView.startAnimation(rotation);
    }

    @Override
    public ApplicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.locker_item_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    public boolean checkLockedItem(String checkApp, Set<String> locked) {
        return locked != null && locked.contains(checkApp);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView applicationName;
        private ImageView icon;
        private ToggleButton switchView;

        public ViewHolder(View v) {
            super(v);
            applicationName = v.findViewById(R.id.applicationName);
            icon = v.findViewById(R.id.icon);
            switchView = v.findViewById(R.id.switchView);
        }

        public void onBind(int position) {
            final Set<String> locked = Prefs.with(itemView.getContext()).getLocked();

            final AppInfo appInfo = installedApps.get(position);
            applicationName.setText(appInfo.getName());
            icon.setImageDrawable(appInfo.getIcon());

            final String packageName = appInfo.getPackageName();
            boolean isLocked = checkLockedItem(packageName, locked);
            switchView.setChecked(isLocked);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppLockInitializer.initAndForcePermissionDialog(activity);

                    boolean selected = !switchView.isChecked();
                    switchView.setChecked(selected);

                    Prefs prefs = Prefs.with(itemView.getContext());
                    if (selected) {
                        prefs.addLocked(packageName);
                        if (locked != null) {
                            locked.add(packageName);
                        }
                    } else {
                        prefs.removeLocked(packageName);
                        if (locked != null) {
                            locked.remove(packageName);
                        }
                    }
                    ApplicationListAdapter.animate(switchView);
                }
            });
        }
    }
}