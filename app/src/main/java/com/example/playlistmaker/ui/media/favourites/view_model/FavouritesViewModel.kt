package com.example.playlistmaker.ui.media.favourites.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.creator.TYPE_FAVOURITES
import com.example.playlistmaker.domain.media.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.favourites.models.FavouritesState
import com.example.playlistmaker.ui.search.models.TrackTriggerState
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val favouritesInteractor: FavouritesInteractor
) : ViewModel() {

    private val screenLiveData = MutableLiveData<FavouritesState>(FavouritesState.Loading)
    fun observeScreenState(): LiveData<FavouritesState> = screenLiveData

    private var showTrackTrigger = SingleLiveEvent<TrackTriggerState>()
    fun observeShowTrackTrigger(): LiveData<TrackTriggerState> = showTrackTrigger

    fun loadData() {
        render(FavouritesState.Loading)

        viewModelScope.launch {
            favouritesInteractor
                .favoritesTracks()
                .collect { tracks ->
                    processFavourites(tracks)
                }
        }
    }

    fun startActiviryPlayer(track: Track, fromType: String) {
        val state = when (fromType) {
            TYPE_FAVOURITES -> startPlayerFromFavourite(track)
            else -> startPlayerFromEmpty()
        }
        render(state)
    }

    private fun startPlayerFromFavourite(track: Track): TrackTriggerState {
        return TrackTriggerState.Favourites(track.trackId)
    }

    private fun startPlayerFromEmpty(): TrackTriggerState {
        return TrackTriggerState.Empty()
    }

    private fun processFavourites(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            render(FavouritesState.Empty(""))
        } else {
            render(FavouritesState.Content(tracks))
        }
    }

    private fun render(state: FavouritesState) {
        screenLiveData.postValue(state)
    }

    private fun render(state: TrackTriggerState) {
        showTrackTrigger.postValue(state)
    }
}