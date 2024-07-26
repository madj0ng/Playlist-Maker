package com.example.playlistmaker.ui.media.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.media.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.ui.media.playlist.models.PlaylistState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val screenLiveData = MutableLiveData<PlaylistState>(PlaylistState.Loading)
    fun observeScreenState(): LiveData<PlaylistState> = screenLiveData

    fun loadData() {
        viewModelScope.launch {
            playlistInteractor
                .loadFavourites()
                .collect { pair ->
                    loadResult(pair.first, pair.second)
                }
        }
    }

    private fun render(state: PlaylistState) {
        screenLiveData.postValue(state)
    }

    private fun loadResult(dbalbums: List<Album>?, errorMessage: String?) {
        val albums = mutableListOf<Album>()
        if (dbalbums != null) {
            albums.clear()
            albums.addAll(dbalbums)
        }
        when {
            errorMessage != null -> {
                render(PlaylistState.Error(errorMessage))
            }

            (albums.isNotEmpty()) -> {
                render(PlaylistState.Content(albums))
            }

            else -> {
                render(PlaylistState.Empty(""))
            }
        }
    }
}