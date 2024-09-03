package com.example.playlistmaker.ui.playlistadd.view_model

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlistadd.PlaylistAddInteractor
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.ui.album.model.AlbumState
import com.example.playlistmaker.ui.playlistadd.models.AlbumDialogState
import com.example.playlistmaker.util.stringReplace
import kotlinx.coroutines.launch

open class PlaylistAddViewModel(
    private val application: Application,
    private val playlistAddInteractor: PlaylistAddInteractor
) : ViewModel() {

    private var album = Album(0L, "", "", null, 0, 0L)

    private val albumState = MutableLiveData<AlbumState>(AlbumState.Loading)
    fun observerAlbumState(): LiveData<AlbumState> = albumState

    private val albumName = MutableLiveData<Boolean>()
    fun observerAlbumName(): LiveData<Boolean> = albumName

    private val albumDescription = MutableLiveData<Boolean>()
    fun observerAlbumDescription(): LiveData<Boolean> = albumDescription

    private val albumUri = MutableLiveData<Uri?>()
    fun observerAlbumUri(): LiveData<Uri?> = albumUri

    private val toastState = MutableLiveData<String>()
    fun observerToast(): LiveData<String> = toastState

    val dialogState = MutableLiveData<AlbumDialogState>(AlbumDialogState.None(true))
    fun observerDialog(): LiveData<AlbumDialogState> = dialogState

    fun changeAlbumState(state: AlbumState) {
        albumState.postValue(state)
        when (state) {
            is AlbumState.Content -> {
                changeAlbum(state.data)
            }

            else -> {}
        }
    }

    fun changeAlbumName(changedText: String?) {
        if (isNewName(changedText ?: "")) {
            album = album.copy(name = changedText ?: "")
            albumName.postValue(album.name.isNotEmpty())
        }
    }

    fun changeAlbumDescription(changedText: String?) {
        if (isNewDescription(changedText ?: "")) {
            album = album.copy(description = changedText ?: "")
            albumDescription.postValue(album.description.isNotEmpty())
        }
    }

    fun changeAlbumUri(uri: Uri?) {
        if (isNewUri(uri)) {
            album = album.copy(uri = uri)
            albumUri.postValue(album.uri)
        }
    }

    private fun changeAlbum(newAlbum: Album) {
        if (isNewAlbum(newAlbum)) {
            album = newAlbum.copy()
            albumName.postValue(album.name.isNotEmpty())
            albumDescription.postValue(album.description.isNotEmpty())
            albumUri.postValue(album.uri)
        }
    }

    fun setToast(message: String) {
        toastState.postValue(message)
    }

    open fun albumOnClick() {
        albumCreate()
    }

    open fun onBackPressed() {
        if (album.name.isNotEmpty() ||
            album.description.isNotEmpty() ||
            album.uri != null
        ) {
            dialogState.postValue(AlbumDialogState.Show(true))
        } else {
            dialogState.postValue(AlbumDialogState.None(false))
        }
    }

    fun setDialogResult(isEnabled: Boolean) {
        dialogState.postValue(AlbumDialogState.None(isEnabled))
    }

    private fun albumCreate() {
        if (album.name.isNotEmpty()) {
            viewModelScope.launch {
                playlistAddInteractor
                    .saveImageToPrivateStorage(album.uri)
                    .collect { uri ->
                        album = album.copy(uri = uri)
                        playlistAddInteractor
                            .albumCreate(album)
                            .collect {
                                val message =
                                    stringReplace(application, it, album.name)
                                if (message != null) {
                                    setToast(message)
                                }
                                // Закрытие
                                setDialogResult(false)
                            }
                    }
            }
        }
    }

    fun albumUpdate(oldAlbum: Album) {
        viewModelScope.launch {
            if (album.name.isNotEmpty() &&
                (oldAlbum.uri != album.uri || oldAlbum.name != album.name || oldAlbum.description != album.description)
            ) {
                if (oldAlbum.uri != album.uri) {
                    // Удаляем файл обложки
                    playlistAddInteractor.deleteImageFromPrivateStorage(oldAlbum.uri)
                    // Сохраняем файл обложки во внутреннее хранилище
                    playlistAddInteractor
                        .saveImageToPrivateStorage(album.uri)
                        .collect { uri ->
                            album = album.copy(uri = uri)
                        }
                }
                albumUpdateRun(album)
            }
        }
    }

    private suspend fun albumUpdateRun(album: Album) {
        playlistAddInteractor
            .albumUpdate(album)
            .collect {
                // Закрытие
                setDialogResult(false)
            }
    }

    private fun isNewName(newText: String): Boolean = (newText != album.name)

    private fun isNewDescription(newText: String): Boolean = (newText != album.description)

    private fun isNewUri(newUri: Uri?): Boolean = (newUri != album.uri)

    private fun isNewAlbum(newAlbum: Album): Boolean = (newAlbum != album)
}