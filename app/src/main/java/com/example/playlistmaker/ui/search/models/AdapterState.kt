package com.example.playlistmaker.ui.search.models

import com.example.playlistmaker.domain.search.model.Track

sealed interface AdapterState {
    data class History(val data: List<Track> = listOf()): AdapterState
    data class Search(val data: List<Track> = listOf()): AdapterState
}
