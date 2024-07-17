package com.example.playlistmaker.ui.media.favourites.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.FormatUtils

class FavouritesAdapter(
    private val clickListener: FavouritesClickListener
) : RecyclerView.Adapter<FavouritesViewHolder>() {

    private var tracks = mutableListOf<Track>()

    fun setList(list: List<Track>){
        this.tracks.clear()
        this.tracks.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val binding = TrackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouritesViewHolder(
            binding = binding,
            clickListener = clickListener,
            formatUtils = FormatUtils
        )
    }
    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    fun interface FavouritesClickListener {
        fun onTrackClick(track: Track)
    }
}