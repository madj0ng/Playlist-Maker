package com.example.playlistmaker.data.media.favourites

import com.example.playlistmaker.domain.media.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track

class FavouritesRepositoryImpl: FavouritesRepository {
    override fun getFavourites(): List<Track> {
        return mutableListOf()
    }
}