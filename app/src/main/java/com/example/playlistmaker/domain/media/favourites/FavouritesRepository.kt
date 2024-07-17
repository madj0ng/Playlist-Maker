package com.example.playlistmaker.domain.media.favourites

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {

    fun favoritesTracks(): Flow<List<Track>>
}