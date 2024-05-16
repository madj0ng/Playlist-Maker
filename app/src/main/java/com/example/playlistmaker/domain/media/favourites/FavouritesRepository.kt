package com.example.playlistmaker.domain.media.favourites

import com.example.playlistmaker.domain.search.model.Track

interface FavouritesRepository {
    fun getFavourites(): List<Track>
}