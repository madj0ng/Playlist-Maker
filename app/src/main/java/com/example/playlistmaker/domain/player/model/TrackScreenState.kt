package com.example.playlistmaker.domain.player.model

import com.example.playlistmaker.domain.search.model.Track

sealed class TrackScreenState {
    data object Loading : TrackScreenState()
    data class Content(val track: Track) : TrackScreenState()
}