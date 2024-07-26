package com.example.playlistmaker.ui.media.playlist.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.AlbumViewBinding
import com.example.playlistmaker.domain.playlistadd.model.Album
import org.koin.java.KoinJavaComponent.getKoin

class PlaylistAdapter(
    private val clickListener: PlaylistClickListener
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var items = mutableListOf<Album>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = AlbumViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(
            binding = binding,
            clickListener = clickListener,
            formatUtils = getKoin().get()
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(list: List<Album>) {
        this.items.clear()
        this.items.addAll(list)
    }

    fun interface PlaylistClickListener {
        fun onAlbumClick(album: Album)
    }
}