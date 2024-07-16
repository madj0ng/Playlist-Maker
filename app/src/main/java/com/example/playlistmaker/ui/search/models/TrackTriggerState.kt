package com.example.playlistmaker.ui.search.models

import com.example.playlistmaker.creator.TYPE_FAVOURITES
import com.example.playlistmaker.creator.TYPE_HISTORY
import com.example.playlistmaker.creator.TYPE_SEARCH

sealed interface TrackTriggerState {
    data class Search(
        val trackId: Int,
        val dataType: String = TYPE_SEARCH
    ) : TrackTriggerState

    data class History(
        val trackId: Int,
        val dataType: String = TYPE_HISTORY
    ) : TrackTriggerState

    data class Favourites(
        val trackId: Int,
        val dataType: String = TYPE_FAVOURITES
    ) : TrackTriggerState

    data class Empty(
        val trackId: Int = 0,
        val dataType: String = ""
    ) : TrackTriggerState
}
