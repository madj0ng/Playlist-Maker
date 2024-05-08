package com.example.playlistmaker.ui.search.activity

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.FormatUtils

class SearchViewHolder(
    private val binding: TrackViewBinding,  //itemView: View,
    private val clickListener: SearchAdapter.SearchClickListener
) : RecyclerView.ViewHolder(binding.root) { //(itemView) {
    companion object {
        // Округление в пикселяхr
        const val IMG_RADIUS_PX = 2F
    }

    // Значение закругления риснука в dp
    private val imgRadius = FormatUtils.dpToPx(IMG_RADIUS_PX, itemView.context)

//    private val trackName = itemView.findViewById<TextView>(R.id.trackName)
//    private val artistName = itemView.findViewById<TextView>(R.id.artistName)
//    private val trackTime = itemView.findViewById<TextView>(R.id.trackTime)
//    private val trackImage = itemView.findViewById<ImageView>(R.id.trackImage)

    fun bind(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = FormatUtils.formatLongToTrakTime(track.trackTimeMillis)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(imgRadius))
            .into(binding.trackImage)

        // Событие нажатия кнопки
        itemView.setOnClickListener { clickListener.onTrackClick(track) }
    }
}