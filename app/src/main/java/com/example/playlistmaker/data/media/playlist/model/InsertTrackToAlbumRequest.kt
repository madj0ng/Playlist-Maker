package com.example.playlistmaker.data.media.playlist.model

import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track

data class InsertTrackToAlbumRequest(
    val track: Track,
    val album: Album,
)
