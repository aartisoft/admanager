package com.admanager.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.admanager.sample.R;

/**
 * Created by Gust on 20.11.2018.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ex_inters).setOnClickListener(this);
        findViewById(R.id.ex_face_6_sec).setOnClickListener(this);
        findViewById(R.id.ex_banner_demo).setOnClickListener(this);
        findViewById(R.id.ex_recycler_view_admob_default).setOnClickListener(this);
        findViewById(R.id.ex_recycler_view_default).setOnClickListener(this);
        findViewById(R.id.ex_recycler_view_density).setOnClickListener(this);
        findViewById(R.id.ex_recycler_view_grid).setOnClickListener(this);
        findViewById(R.id.ex_recycler_view_bignative).setOnClickListener(this);
        findViewById(R.id.ex_recycler_view_custom_design).setOnClickListener(this);
        findViewById(R.id.ex_recycler_view_custom).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ex_inters:
                startActivity(new Intent(this, MainIntersActivity.class));
                break;
            case R.id.ex_face_6_sec:
                startActivity(new Intent(this, Facebook6SecsActivity.class));
                break;
            case R.id.ex_banner_demo:
                startActivity(new Intent(this, BannerDemoActivity.class));
                break;
            case R.id.ex_recycler_view_admob_default:
                startActivity(new Intent(this, RecyclerViewAdmobDefaultActivity.class));
                break;
            case R.id.ex_recycler_view_default:
                startActivity(new Intent(this, RecyclerViewDefaultActivity.class));
                break;
            case R.id.ex_recycler_view_density:
                startActivity(new Intent(this, RecyclerViewDensityActivity.class));
                break;
            case R.id.ex_recycler_view_grid:
                startActivity(new Intent(this, RecyclerViewGridActivity.class));
                break;
            case R.id.ex_recycler_view_bignative:
                startActivity(new Intent(this, RecyclerViewBigNativeActivity.class));
                break;
            case R.id.ex_recycler_view_custom_design:
                startActivity(new Intent(this, RecyclerViewCustomDesignActivity.class));
                break;
            case R.id.ex_recycler_view_custom:
                startActivity(new Intent(this, RecyclerViewCustomActivity.class));
                break;
        }
    }
}
