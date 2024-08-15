package com.example.playlistmaker.ui.media.playlist.fragment

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.AlbumViewBinding
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.util.FormatUtils

class PlaylistViewHolder(
    private val binding: AlbumViewBinding,
    private val clickListener: PlaylistAdapter.PlaylistClickListener,
    formatUtils: FormatUtils
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        // Округление в пикселяхr
        private const val IMG_RADIUS_PX = 2F
    }

    // Значение закругления риснука в dp
    private val imgRadius = formatUtils.dpToPx(IMG_RADIUS_PX, itemView.context)

    fun bind(album: Album) {
        binding.tvName.text = album.name
        binding.tvCount.text = album.tracksCount.toString()
        binding.tvCountName.text = itemView.context.getText(R.string.playlist_item)

        Glide.with(itemView)
            .load(album.uri)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(imgRadius))
            .into(binding.ivAlbum)

        // Событие нажатия кнопки
        itemView.setOnClickListener { clickListener.onAlbumClick(album) }
    }
}