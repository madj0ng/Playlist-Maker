package com.example.playlistmaker.ui.media.playlist.models

import com.example.playlistmaker.domain.search.model.Track

sealed interface PlaylistState {
    data object Loading : PlaylistState

    data class Content(val data: List<Track>) : PlaylistState

    data class Error(val errorMessage: String) : PlaylistState

    data class Empty(val message: String) : PlaylistState
}