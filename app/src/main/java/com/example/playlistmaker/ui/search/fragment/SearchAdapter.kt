package com.example.playlistmaker.ui.search.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.FormatUtils

class SearchAdapter(
    private val clickListener: SearchClickListener
) : RecyclerView.Adapter<SearchViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = TrackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(
            binding = binding,
            clickListener = clickListener,
            formatUtils = FormatUtils
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    fun interface SearchClickListener {
        fun onTrackClick(track: Track)
    }
}