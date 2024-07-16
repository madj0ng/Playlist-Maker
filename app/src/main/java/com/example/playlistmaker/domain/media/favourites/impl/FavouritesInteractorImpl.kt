package com.example.playlistmaker.domain.media.favourites.impl

import com.example.playlistmaker.domain.media.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.media.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavouritesInteractorImpl(
    private val favouritesRepository: FavouritesRepository //FavouritesRepositoryOld
) : FavouritesInteractor {

    override fun favoritesTracks(): Flow<List<Track>> {
        return favouritesRepository.favoritesTracks()
    }
}