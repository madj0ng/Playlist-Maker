package com.example.playlistmaker.ui.media.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.media.playlist.PlaylistInteractor
import com.example.playlistmaker.ui.media.playlist.models.PlaylistState

class PlaylistViewModel(
    playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    init {
        playlistInteractor.loadFavourites(
            onComplete = { trakList -> render(PlaylistState.Content(trakList)) },
            onError = { message -> PlaylistState.Empty(message) }
        )
    }

    private val screenLiveData = MutableLiveData<PlaylistState>(PlaylistState.Loading)
    fun observeScreenState(): LiveData<PlaylistState> = screenLiveData

    private fun render(state: PlaylistState) {
        screenLiveData.postValue(state)
    }
}