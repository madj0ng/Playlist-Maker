package com.example.playlistmaker.ui.player.models

import com.example.playlistmaker.domain.search.model.Track

sealed interface PlayerState {
    data object Loading : PlayerState

    data class Content(val track: Track) : PlayerState

    data class Error(val errorMessage: String) : PlayerState

    data class Empty(val message: String) : PlayerState
}
