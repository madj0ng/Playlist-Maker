package com.example.playlistmaker.ui.player.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.AlbumRectViewBinding
import com.example.playlistmaker.domain.playlistadd.model.Album
import org.koin.java.KoinJavaComponent.getKoin


class PlayerAdapter(
    private val clickListener: PlayerClickListener
) : RecyclerView.Adapter<PlayerViewHolder>() {

    private var items = mutableListOf<Album>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding =
            AlbumRectViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(
            binding = binding,
            clickListener = clickListener,
            formatUtils = getKoin().get()
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(list: List<Album>) {
        this.items.clear()
        this.items.addAll(list)
    }

    fun interface PlayerClickListener {
        fun onAlbumClick(album: Album)
    }
}
