package com.example.playlistmaker.ui.media.playlist.models

import com.example.playlistmaker.domain.playlistadd.model.Album

sealed interface PlaylistState {
    data object Loading : PlaylistState

    data class Content(val data: List<Album>) : PlaylistState

    data class Error(val errorMessage: String) : PlaylistState

    data class Empty(val message: String) : PlaylistState
}