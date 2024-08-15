package com.example.playlistmaker.data.media.playlist.model

import com.example.playlistmaker.domain.search.model.Track

data class InsertTrackInAlbumRequest(
    val albumId: Long,
    val track: Track,
)
