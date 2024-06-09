package com.example.playlistmaker.ui.media.favourites.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.media.favourites.FavouritesInteractor
import com.example.playlistmaker.ui.media.favourites.models.FavouritesState

class FavouritesViewModel(
    favouritesInteractor: FavouritesInteractor
) : ViewModel() {

    init {
        favouritesInteractor.loadFavourites(
            onComplete = { trakList -> render(FavouritesState.Content(trakList)) },
            onError = { message -> FavouritesState.Empty(message) }
        )
    }

    private val screenLiveData = MutableLiveData<FavouritesState>(FavouritesState.Loading)
    fun observeScreenState(): LiveData<FavouritesState> = screenLiveData

    private fun render(state: FavouritesState) {
        screenLiveData.postValue(state)
    }
}