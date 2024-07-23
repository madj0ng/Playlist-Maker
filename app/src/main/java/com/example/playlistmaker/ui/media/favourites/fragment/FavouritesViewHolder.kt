package com.example.playlistmaker.ui.media.favourites.fragment

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.FormatUtils

class FavouritesViewHolder(
    private val binding: TrackViewBinding,
    private val clickListener: FavouritesAdapter.FavouritesClickListener,
    private val formatUtils: FormatUtils
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        // Округление в пикселяхr
        private const val IMG_RADIUS_PX = 2F
    }

    // Значение закругления риснука в dp
    private val imgRadius = formatUtils.dpToPx(IMG_RADIUS_PX, itemView.context)

    fun bind(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = formatUtils.formatLongToTrakTime(track.trackTimeMillis)

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