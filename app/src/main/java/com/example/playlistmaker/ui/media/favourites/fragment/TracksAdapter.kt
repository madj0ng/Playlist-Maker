package com.example.playlistmaker.ui.media.favourites.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.album.model.ListItemChangeState
import org.koin.java.KoinJavaComponent.getKoin

class TracksAdapter(
    private val clickListener: FavouritesClickListener,
    private val longClickListener: ItemLongClickListener,
) : RecyclerView.Adapter<TracksViewHolder>() {

    private var tracks = mutableListOf<Track>()

    fun setList(list: List<Track>) {
        this.tracks.clear()
        this.tracks.addAll(list)
    }

    fun deleteTrack(position: Int) {
        this.tracks.removeAt(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val binding = TrackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TracksViewHolder(
            binding = binding,
            clickListener = clickListener,
            formatUtils = getKoin().get()
        )
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnLongClickListener {
            longClickListener.onItemLongClick(
                ListItemChangeState(tracks[position], position, position, getItemCount())
            )
        }
    }

    fun interface FavouritesClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface ItemLongClickListener {
        fun onItemLongClick(listItem: ListItemChangeState): Boolean
    }
}