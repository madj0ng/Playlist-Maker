package com.example.playlistmaker.presentation.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.utils.FormatUtils

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        // Округление в пикселях
        const val IMG_RADIUS_PX = 2F
    }

    // Значение закругления риснука в dp
    //1 вариант(после тестов, получил, что значение соответствует варианту из спринта 10)
    //private val imgRadius = itemView.context.resources.getDimension(R.dimen.image_track_radius).toInt()
    //2 вариант из спринта 10
    private val imgRadius = FormatUtils.dpToPx(IMG_RADIUS_PX, itemView.context)

    private val trackName = itemView.findViewById<TextView>(R.id.trackName)
    private val artistName = itemView.findViewById<TextView>(R.id.artistName)
    private val trackTime = itemView.findViewById<TextView>(R.id.trackTime)
    private val trackImage = itemView.findViewById<ImageView>(R.id.trackImage)

    fun bind(item: Track) {
        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = item.trackTime

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(imgRadius))
            .into(trackImage)
    }
}