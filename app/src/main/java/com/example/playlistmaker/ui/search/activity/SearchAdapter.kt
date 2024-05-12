package com.example.playlistmaker.ui.search.activity

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.search.model.Track
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

class SearchAdapter(
    private val clickListener: SearchClickListener
) : RecyclerView.Adapter<SearchViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return getKoin().get { parametersOf(parent, clickListener) }
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    fun interface SearchClickListener {
        fun onTrackClick(track: Track)
    }
}