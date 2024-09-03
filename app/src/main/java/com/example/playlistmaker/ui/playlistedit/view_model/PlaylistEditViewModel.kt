package com.example.playlistmaker.ui.playlistedit.view_model

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlistadd.PlaylistAddInteractor
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.ui.album.model.AlbumState
import com.example.playlistmaker.ui.playlistadd.models.AlbumDialogState
import com.example.playlistmaker.ui.playlistadd.view_model.PlaylistAddViewModel
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    application: Application,
    playlistAddInteractor: PlaylistAddInteractor,
    private val albumId: Long
) : PlaylistAddViewModel(application, playlistAddInteractor) {

    private var album = Album(0L, "", "", null, 0, 0L)

    init {
        viewModelScope.launch {
            playlistAddInteractor
                .getAlbum(albumId)
                .collect { pair ->
                    loadResult(pair.first, pair.second)
                }
        }
    }

    private fun loadResult(album: Album?, errorMessage: String?) {
        if (album != null) {
            this.album = album
            changeAlbumState(AlbumState.Content(album))
        }

        if (errorMessage != null) {
            setToast(errorMessage)
        }
    }

    override fun albumOnClick() {
        albumUpdate(this.album)
    }

    override fun onBackPressed() {
        dialogState.postValue(AlbumDialogState.None(false))
    }
}