package com.example.playlistmaker.ui.playlistadd.view_model

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.converters.AlbumModelConverter
import com.example.playlistmaker.domain.playlistadd.PlaylistAddInteractor
import com.example.playlistmaker.ui.playlistadd.models.AlbumDialogState
import com.example.playlistmaker.ui.playlistadd.models.AlbumState
import com.example.playlistmaker.util.stringReplace
import kotlinx.coroutines.launch

class PlaylistAddViewModel(
    private val application: Application,
    private val playlistAddInteractor: PlaylistAddInteractor,
    private val albumModelConverter: AlbumModelConverter
) : AndroidViewModel(application) {

    private var albumState = MutableLiveData<AlbumState>(AlbumState("", "", null))
    fun observerAlbum(): LiveData<AlbumState> = albumState

    private var toastState = MutableLiveData<String>()
    fun observerToast(): LiveData<String> = toastState

    private var dialogState = MutableLiveData<AlbumDialogState>(AlbumDialogState.None(true))
    fun observerDialog(): LiveData<AlbumDialogState> = dialogState

    fun changeAlbumName(changedText: String?) {
        albumState.postValue(albumState.value?.copy(name = changedText ?: ""))
    }

    fun changeAlbumDescription(changedText: String?) {
        albumState.postValue(albumState.value?.copy(description = changedText ?: ""))
    }

    fun changeAlbumUri(uri: Uri?) {
        albumState.postValue(albumState.value?.copy(uri = uri))
    }

    fun albumCreate() {
        if (albumState.value != null) {
            viewModelScope.launch {
                // Сохраняем файл во внутренее хранилище
                playlistAddInteractor
                    .saveImageToPrivateStorage(albumState.value!!.uri)
                    .collect { uri ->
                        // Сохраняем Playlist в БД
                        playlistAddInteractor
                            .albumCreate(albumModelConverter.map(albumState.value!!.copy(uri = uri)))
                            .collect {
                                val s = stringReplace(application, it, albumState.value!!.name)
                                if (s != null) {
                                    toastState.postValue(s!!)
                                }
                                // Закрытие
                                setDialogResult(false)
                            }
                    }
            }
        }
    }

    fun onBackPressed() {
        if (albumState.value != null) {
            if (albumState.value!!.name.isNotEmpty() ||
                albumState.value!!.description.isNotEmpty() ||
                albumState.value!!.uri != null
            ) {
                dialogState.postValue(AlbumDialogState.Show(true))
            } else {
                dialogState.postValue(AlbumDialogState.None(false))
            }
        }
    }

    fun setDialogResult(isEnabled: Boolean) {
        dialogState.postValue(AlbumDialogState.None(isEnabled))
    }
}