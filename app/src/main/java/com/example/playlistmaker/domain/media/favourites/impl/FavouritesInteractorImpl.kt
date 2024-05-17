package com.example.playlistmaker.domain.media.favourites.impl

import com.example.playlistmaker.domain.media.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.media.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track

class FavouritesInteractorImpl(
    private val favouritesRepository: FavouritesRepository
) : FavouritesInteractor {

    override fun loadFavourites(
        onComplete: (List<Track>) -> Unit,
        onError: (String) -> Unit) {
        val list = favouritesRepository.getFavourites()
        if(list.isEmpty()){
            onError.invoke("")
        }else{
            onComplete.invoke(list)
        }
    }
}