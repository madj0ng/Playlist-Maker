package com.example.playlistmaker.domain.media.favourites

import com.example.playlistmaker.domain.search.model.Track

interface FavouritesInteractor {
    fun loadFavourites(
        onComplete: (List<Track>) -> Unit,
        onError: (String) -> Unit
    )
}