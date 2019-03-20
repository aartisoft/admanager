package com.admanager.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.admanager.config.RemoteConfigHelper;

import java.util.ArrayList;
import java.util.List;

abstract class ABaseAdapter<T, VH extends BindableViewHolder<T>, CONF extends AdmAdapterConfiguration<?>> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ABaseAdapter";

    private final Activity activity;
    boolean show_native;
    //native
    int DEFAULT_NO_OF_DATA_BETWEEN_ADS;
    private List<RowWrapper> rowWrappers;
    private List<T> data;
    CONF configuration;

    @LayoutRes
    private int layout;
    private boolean isLoading = false;
    private boolean isLoadingPage = false;

    ABaseAdapter(final Activity activity, @LayoutRes int layout) {
        this(activity, layout, new ArrayList<T>());
    }

    ABaseAdapter(final Activity activity, @LayoutRes int layout, List<T> data) {
        this(activity, layout, data, false);
    }

    ABaseAdapter(final Activity activity, @LayoutRes int layout, List<T> data, boolean show_native) {
        this.activity = activity;
        this.data = data == null ? new ArrayList<T>() : data;
        this.show_native = show_native || RemoteConfigHelper.isTestMode();
        this.layout = layout;
        this.configuration = configure();
        if (this.configuration == null) {
            this.configuration = createDefaultConfiguration();
        }
        fillDefaultTypeOfConfiguration();

        int gridSize = configuration.getGridSize();

        if (gridSize > 1) {
            DEFAULT_NO_OF_DATA_BETWEEN_ADS = gridSize * configuration.getDensityForGrid();
        } else if (gridSize == 1) {
            DEFAULT_NO_OF_DATA_BETWEEN_ADS = configuration.getDensity();
        } else {
            DEFAULT_NO_OF_DATA_BETWEEN_ADS = 0;
        }
        rowWrappers = getRowWrappers();
    }

    protected abstract void fillDefaultTypeOfConfiguration();

    protected abstract CONF createDefaultConfiguration();

    protected CONF configure() {
        return null;
    }

    public final int getGridSize() {
        return configuration.getGridSize();
    }

    protected abstract VH createViewHolder(View view);

    @Override
    public final int getItemViewType(int position) {
        return rowWrappers.get(position).type.ordinal();
    }

    @Override
    public final int getItemCount() {
        return rowWrappers.size();
    }

    @Override
    @CallSuper
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;

        final Context context = parent.getContext();
        if (viewType == RowWrapper.Type.LOADING.ordinal() || viewType == RowWrapper.Type.LOADING_PAGE.ordinal()) {
            ProgressBar pb = new ProgressBar(context);
            LinearLayout.LayoutParams pbParam = new LinearLayout.LayoutParams(80, 80);
            pb.setLayoutParams(pbParam);

            RelativeLayout rel = new RelativeLayout(context);
            RelativeLayout.LayoutParams relParams;
            if (viewType == RowWrapper.Type.LOADING.ordinal()) {
                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                relParams = new RelativeLayout.LayoutParams(dm.widthPixels, dm.heightPixels);
            } else {
                relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            rel.setLayoutParams(relParams);

            RelativeLayout.LayoutParams relParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relParam.addRule(RelativeLayout.CENTER_IN_PARENT);
            rel.addView(pb, relParam);

            view = rel;
            holder = new LoadingViewHolder(view);
        } else if (viewType == RowWrapper.Type.LIST.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            holder = createViewHolder(view);
        }
        return holder;
    }

    @Override
    @CallSuper
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (rowWrappers.get(position).type.ordinal() == RowWrapper.Type.LIST.ordinal()) {
            int pos = rowWrappers.get(position).listIndex;
            if (holder instanceof BindableViewHolder) {
                ((BindableViewHolder) holder).bindTo(activity, data.get(pos), pos);
            }
        }
    }

    final boolean notLoading() {
        return !isLoading && !isLoadingPage;
    }

    public final void loadingMore() {
        this.isLoadingPage = true;
        this.rowWrappers = getRowWrappers();
        notifyDataSetChanged();
    }

    public final void setLoadingFullScreen() {
        this.isLoading = true;
        this.rowWrappers = getRowWrappers();
        notifyDataSetChanged();
    }

    public final void loaded() {
        this.isLoading = false;
        this.isLoadingPage = false;
        this.rowWrappers = getRowWrappers();
        notifyDataSetChanged();
    }

    public final List<T> getData() {
        return data;
    }

    public final void setData(List<T> data) {
        this.data = data;
        this.rowWrappers = getRowWrappers();
        notifyDataSetChanged();
    }

    public final void removeAll() {
        this.data.clear();
        this.data = new ArrayList<>();
        rowWrappers = getRowWrappers();
        notifyDataSetChanged();
    }

    final void refreshRowWrappers() {
        rowWrappers = getRowWrappers();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private List<RowWrapper> getRowWrappers() {
        List<RowWrapper> rowWrappers = new ArrayList<>();
        rowWrappers.clear();
        if (isLoading) {
            rowWrappers.add(new RowWrapper(RowWrapper.Type.LOADING));
            return rowWrappers;
        }
        if (data == null || data.size() == 0) {
            if (isLoadingPage) {
                rowWrappers.add(new RowWrapper(RowWrapper.Type.LOADING_PAGE));
            }
            return rowWrappers;
        }

        for (int j = 0; j < data.size(); j++) {
            if (DEFAULT_NO_OF_DATA_BETWEEN_ADS > 0 && show_native && j > 0 && (j % DEFAULT_NO_OF_DATA_BETWEEN_ADS) == 0) {
                rowWrappers.add(new RowWrapper(RowWrapper.Type.NATIVE_AD));
            }

            rowWrappers.add(new RowWrapper(j));
        }
        if (isLoadingPage) {
            rowWrappers.add(new RowWrapper(RowWrapper.Type.LOADING_PAGE));
        }
        return rowWrappers;
    }

    public final void add(T e) {
        add(e, false);
    }

    private void add(T e, boolean isChecked) {
        data.add(e);
        notifyItemInserted(data.size());
        if (!isChecked) {
            rowWrappers = getRowWrappers();
        }
    }

    public final void add(ArrayList<T> data) {
        for (T d : data) {
            add(d, true);
        }
        rowWrappers = getRowWrappers();
    }

    public final GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (isLoading) {
                    return configuration.getGridSize();
                } else if (!show_native) {
                    return 1;
                } else if (getItemViewType(position) == RowWrapper.Type.LIST.ordinal()) {
                    return 1;
                }
                return configuration.getGridSize();
            }
        };
    }

    protected final int getListIndex(int position) {
        return rowWrappers.get(position).listIndex;

    }

    boolean isTestMode() {
        return RemoteConfigHelper.isTestMode();
    }

    static class RowWrapper {

        final int listIndex;
        final Type type;

        RowWrapper(int listIndex) {
            this(listIndex, Type.LIST);
        }

        RowWrapper(Type type) {
            this(-999999, type);
        }

        RowWrapper(int listIndex, Type type) {
            this.type = type;
            this.listIndex = listIndex;
        }

        public enum Type {
            LOADING, LOADING_PAGE, LIST, NATIVE_AD
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {

        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
