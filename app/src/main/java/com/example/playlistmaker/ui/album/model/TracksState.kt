package com.example.playlistmaker.ui.album.model

import com.example.playlistmaker.domain.search.model.Track

sealed interface TracksState {
    data object Loading : TracksState

    data class Content(val data: List<Track>) : TracksState

    data class Error(val errorMessage: String) : TracksState

    data class Empty(val message: String) : TracksState
}