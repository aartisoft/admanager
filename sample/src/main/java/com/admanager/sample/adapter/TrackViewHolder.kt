package com.admanager.sample.adapter

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.admanager.recyclerview.BindableViewHolder
import com.admanager.sample.R

class TrackViewHolder(itemView: View) :
    BindableViewHolder<TrackModel?>(itemView) {
    private val name: TextView = itemView.findViewById(R.id.name)
    private val trackId: TextView = itemView.findViewById(R.id.trackId)

    override fun bindTo(activity: Activity?, t: TrackModel?, position: Int) {
        t?.let { item ->
            trackId.text = item.trackId.toString()
            name.text = item.name
        }
    }

}