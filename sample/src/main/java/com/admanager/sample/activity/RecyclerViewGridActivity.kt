package com.admanager.sample.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.admanager.sample.R
import com.admanager.sample.adapter.TrackAdapterWithGrid
import com.admanager.sample.adapter.TrackModel
import kotlinx.android.synthetic.main.activity_native_list.*
import java.util.*

/**
 * Created by Gust on 20.11.2018.
 */
class RecyclerViewGridActivity : AppCompatActivity() {
    lateinit var trackAdapter: TrackAdapterWithGrid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_list)
        trackAdapter = TrackAdapterWithGrid(this)
        // create grid layout
        val layout = GridLayoutManager(this, trackAdapter.gridSize)
        layout.spanSizeLookup = trackAdapter.spanSizeLookup

        recyclerView.apply {
            layoutManager = layout
            // set adapter
            adapter = trackAdapter
            // divider
            addItemDecoration(
                DividerItemDecoration(this@RecyclerViewGridActivity, layout.orientation)
            )
        }
        loadTracksAsync()
    }

    private fun loadTracksAsync() {
        trackAdapter.setLoadingFullScreen()
        Handler().postDelayed({
            val data = ArrayList<TrackModel?>()
            for (i in 0..49) {
                data.add(TrackModel(i, "Track_$i"))
            }
            trackAdapter.data = data
            trackAdapter.loaded()
        }, 2000)
    }
}