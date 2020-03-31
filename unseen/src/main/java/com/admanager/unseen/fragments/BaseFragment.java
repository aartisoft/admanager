package com.admanager.unseen.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.unseen.R;
import com.admanager.unseen.adapters.ConversationsAdapter;
import com.admanager.unseen.notiservice.NotiListenerService;
import com.admanager.unseen.notiservice.converters.BaseConverter;
import com.admanager.unseen.notiservice.converters.ConverterData;
import com.admanager.unseen.notiservice.models.Conversation;
import com.admanager.unseen.utils.Utils;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public abstract class BaseFragment extends Fragment {

    private static final String TAG = "MessagesFragment";
    protected Realm realm;
    protected RecyclerView recyclerView;
    protected View noDataContainer;
    protected ImageView type;
    protected TextView typeText;
    protected TabLayout tabLayout;
    private TextView name;
    private View req;
    private boolean hasData;

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        return rootView;
    }

    protected abstract ConversationsAdapter getAdapter(String tag);

    protected boolean showRecyclerView() {
        return true;
    }

    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // todo ilk acilista ALL sekmesinde data olmadigini saniyor

        recyclerView = view.findViewById(R.id.recyclerView);
        noDataContainer = view.findViewById(R.id.noDataContainer);
        type = view.findViewById(R.id.type);
        typeText = view.findViewById(R.id.typeText);
        tabLayout = view.findViewById(R.id.tabLayout);
        realm = Realm.getDefaultInstance();

        req = view.findViewById(R.id.req);
        name = view.findViewById(R.id.name);
        String appName = "";
        try {
            appName = getString(getContext().getApplicationInfo().labelRes);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        name.setText(String.format("%s\n%s", getString(R.string.unseen_perm_req_text_1), getString(R.string.unseen_perm_req_text_2, appName)));

        RecyclerView.LayoutManager layoutManager = getLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        ConversationsAdapter adapter = getAdapter("");
        adapter.setEmptyListener(new RealmRecyclerViewAdapter.EmptyListener() {
            @Override
            public void dataChanged(boolean empty) {
                hasData = !empty;
                updateListStatus(showRecyclerView());
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        if (!(layoutManager instanceof GridLayoutManager)) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 18) {
                    try {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        return;
                    } catch (Throwable e) {
                        Log.e("AccessibilityUtils", "Notification listeners activity not found.", e);
                    }
                }
                try {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Throwable e) {
                    Log.e("AccessibilityUtils", "Accessibility settings not found!", e);
                }
            }
        });

        TabLayout.Tab tab = tabLayout.newTab();
        tab.setTag("");
        tab.setText("All");
        tabLayout.addTab(tab);

        LinkedHashSet<String> typeMap = new LinkedHashSet<>();
        addDataToList(typeMap, true);
        addDataToList(typeMap, false);

        LinkedList<ConverterData> ordered = new LinkedList<>();
        for (String ty : typeMap) {
            ordered.add(NotiListenerService.getConverterData().get(ty));
        }

        for (ConverterData data : ordered) {
            TabLayout.Tab t = tabLayout.newTab();
            t.setCustomView(getCustomTab(data.getTitle(), data.getColor()));
            t.setTag(data.getType());
            tabLayout.addTab(t);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tag = null;
                Object o = tab.getTag();
                if (o instanceof String) {
                    tag = (String) o;
                    Utils.setCornerRadiusBgColorToImageView(type, tag);
                    if (!TextUtils.isEmpty(tag)) {
                        typeText.setText(tag);
                    } else {
                        typeText.setText("");
                    }

                    ConverterData data = NotiListenerService.getConverterData().get(tag);
                    int color = R.color.colorPrimary;
                    int colorDark = R.color.colorPrimaryDark;
                    if (data != null) {
                        color = data.getColor();
                        colorDark = color;
                    }
                    setToolbarColor(color, colorDark);

                } else {
                    System.out.println();
                }

                ConversationsAdapter adapter = (ConversationsAdapter) recyclerView.getAdapter();
                recyclerView.setAdapter(null);
                RealmQuery<Conversation> q = realm.where(Conversation.class);
                if (!TextUtils.isEmpty(tag)) {
                    q = q.equalTo("type", tag);
                }
                RealmResults<Conversation> all = q.findAll().sort("lastMessage.time", Sort.DESCENDING);
                adapter.updateData(all);
                recyclerView.setAdapter(adapter);

//                recyclerView.setAdapter(getAdapter(tag));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setButtonEnableStatus(showRecyclerView());

    }

    private void addDataToList(LinkedHashSet<String> ordered, boolean b) {
        for (Map.Entry<String, Class<? extends BaseConverter>> entry : NotiListenerService.getPackageMap().entrySet()) {
            String pn = entry.getKey();

            boolean installed = Utils.isPackageInstalled(pn, getActivity().getPackageManager());
            if (installed == b) {
                Class<? extends BaseConverter> value = entry.getValue();
                try {
                    BaseConverter converter = value.newInstance();
                    ConverterData data = converter.getData();

                    if (!ordered.contains(data.getType())) {
                        ordered.add(data.getType());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setToolbarColor(int color, int colorDark) {
        GradientDrawable gd = Utils.createGradientDrawable(getContext(), color, 0);
        if (getActivity() == null) {
            return;
        }
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(gd);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Window w = getActivity().getWindow();
            if (w != null) {
                w.setStatusBarColor(ContextCompat.getColor(getContext(), colorDark));
            }
        }
    }

    public View getCustomTab(String text, @ColorRes int color) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        ImageView img = v.findViewById(R.id.img);
        TextView txt = v.findViewById(R.id.txt);

        Utils.setCornerRadiusBgColorToImageView(img, color);
        txt.setText(text);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        realm.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        setButtonEnableStatus(showRecyclerView());
    }

    protected void setButtonEnableStatus(boolean showList) {
        if (name != null) {
            name.setVisibility(showList ? View.GONE : View.VISIBLE);
        }
        if (req != null) {
            req.setVisibility(showList ? View.GONE : View.VISIBLE);
        }
        updateListStatus(showList);
        if (tabLayout != null) {
            tabLayout.setVisibility(!showList ? View.GONE : View.VISIBLE);
        }
    }

    private void updateListStatus(boolean showList) {
        if (recyclerView == null) {
            return;
        }
        if (showList) {
            recyclerView.setVisibility(!hasData ? View.GONE : View.VISIBLE);
            noDataContainer.setVisibility(hasData ? View.GONE : View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noDataContainer.setVisibility(View.GONE);
        }

    }


}
