package com.example.playlistmaker.ui.media.favourites.models

import com.example.playlistmaker.domain.search.model.Track

sealed interface FavouritesState {
    object Loading : FavouritesState

    data class Content(val data: List<Track>) : FavouritesState

    data class Error(val errorMessage: String) : FavouritesState

    data class Empty(val message: String) : FavouritesState
}