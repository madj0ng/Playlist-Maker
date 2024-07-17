package com.example.playlistmaker.domain.media.favourites

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {
    fun favoritesTracks(): Flow<List<Track>>
}