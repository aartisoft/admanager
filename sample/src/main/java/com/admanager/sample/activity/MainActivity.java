package com.admanager.sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.admanager.sample.R;

import java.util.HashMap;

/**
 * Created by Gust on 20.11.2018.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    HashMap<Integer, Class<? extends Activity>> clickMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickMap.put(R.id.ex_inters, MainIntersActivity.class);
        clickMap.put(R.id.ex_face_6_sec, Facebook6SecsActivity.class);
        clickMap.put(R.id.ex_banner_demo, BannerDemoActivity.class);
        clickMap.put(R.id.ex_admob_native, AdmobNativeActivity.class);
        clickMap.put(R.id.ex_face_native, FacebookNativeActivity.class);
        clickMap.put(R.id.ex_recycler_view_admob_default, RecyclerViewAdmobDefaultActivity.class);
        clickMap.put(R.id.ex_recycler_view_default, RecyclerViewDefaultActivity.class);
        clickMap.put(R.id.ex_recycler_view_density, RecyclerViewDensityActivity.class);
        clickMap.put(R.id.ex_recycler_view_grid, RecyclerViewGridActivity.class);
        clickMap.put(R.id.ex_recycler_view_bignative, RecyclerViewBigNativeActivity.class);
        clickMap.put(R.id.ex_recycler_view_custom_design, RecyclerViewCustomDesignActivity.class);
        clickMap.put(R.id.ex_recycler_view_custom, RecyclerViewCustomActivity.class);
        clickMap.put(R.id.ex_sample_main, SampleMainActivity.class);
        clickMap.put(R.id.ex_sample_wastickers, SampleWAStickersActivity.class);
        clickMap.put(R.id.ex_pack_name_opener, PackNameOpenerActivity.class);

        for (Integer i : clickMap.keySet()) {
            findViewById(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Class<? extends Activity> activityClass = clickMap.get(id);
        if (activityClass == null) {
            Toast.makeText(this, "Add activity to map", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent(this, activityClass));
    }
}
