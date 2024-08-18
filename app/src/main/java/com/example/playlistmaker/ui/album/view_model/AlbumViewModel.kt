package com.example.playlistmaker.ui.album.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.TYPE_ALBUM
import com.example.playlistmaker.domain.album.AlbumInteractor
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.album.model.AlbumState
import com.example.playlistmaker.ui.album.model.ListItemChangeState
import com.example.playlistmaker.ui.album.model.TracksState
import com.example.playlistmaker.ui.search.models.TrackTriggerState
import com.example.playlistmaker.util.SingleLiveEvent
import com.example.playlistmaker.util.stringReplace
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val application: Application,
    private val albumId: Long,
    private val albumInteractor: AlbumInteractor,
) : AndroidViewModel(application) {

    private val tracksLiveData = MutableLiveData<TracksState>(TracksState.Loading)
    fun observeTracks(): LiveData<TracksState> = tracksLiveData

    private val albumLiveData = MutableLiveData<AlbumState>(AlbumState.Loading)
    fun observeAlbum(): LiveData<AlbumState> = albumLiveData

    private var showTrackTrigger = SingleLiveEvent<TrackTriggerState>()
    fun observeShowTrackTrigger(): LiveData<TrackTriggerState> = showTrackTrigger

    private var trackListLiveData = SingleLiveEvent<ListItemChangeState>()
    fun observeItemChange(): LiveData<ListItemChangeState> = trackListLiveData

    private var dialogState = SingleLiveEvent<String>()
    fun observeDialog(): LiveData<String> = dialogState

    private var backPressState = SingleLiveEvent<Boolean>()
    fun observeBackPress(): LiveData<Boolean> = backPressState

    private var showEdit = SingleLiveEvent<Long>()
    fun observeShowEdit(): LiveData<Long> = showEdit

    private var toastState = MutableLiveData<String>()
    fun observerToast(): LiveData<String> = toastState

    fun loadData() {
        viewModelScope.launch {
            albumInteractor
                .getAlbumData(albumId)
                .collect { pair ->
                    resultRender(pair.first, pair.second)
                }

            albumInteractor
                .getTracksOfAlbum(albumId)
                .collect { tracks ->
                    setState(TracksState.Content(tracks))
                }
        }
    }

    fun startActiviryPlayer(track: Track, fromType: String) {
        setState(
            when (fromType) {
                TYPE_ALBUM -> TrackTriggerState.Album(track.trackId)
                else -> TrackTriggerState.Empty()
            }
        )
    }

    fun shareApp() {
        viewModelScope.launch {
            albumInteractor
                .shareApp(albumId)
                .collect { pair ->
                    resultRender(pair.first, pair.second)
                }
        }
    }

    fun editAlbum() {
        showEdit.postValue(albumId)
    }

    fun deleteTrackFromAlbum(listItem: ListItemChangeState) {
        viewModelScope.launch {
            albumInteractor
                .deleteTrackFromAlbum(albumId, listItem.track.trackId)
                .collect {
                    if (it) {
                        trackListLiveData.postValue(listItem)
                    }
                }
            albumInteractor
                .getAlbumData(albumId)
                .collect { pair ->
                    resultRender(pair.first, pair.second)
                }
        }
    }

    fun deleteAlbum() {
        viewModelScope.launch {
            albumInteractor
                .deleteAlbum(albumId)
                .collect {
                    if (it) {
                        backPress(it)
                    }
                }
        }
    }

    fun deleteAlbumDialog() {
        if (albumLiveData.value != null && albumLiveData.value is AlbumState.Content) {
            val question = stringReplace(
                application,
                R.string.dialog_album_delete_ask,
                (albumLiveData.value as AlbumState.Content).data.name
            )
            if (question != null) {
                dialogState.postValue(question!!)
            }
        }
    }

    private fun backPress(isBack: Boolean) {
        backPressState.postValue(isBack)
    }

    private fun resultRender(album: Album?, errorMessage: String?) {
        if (album != null) {
            setState(AlbumState.Content(album))
        }

        if (errorMessage != null) {
            setState(AlbumState.Error(errorMessage))
        }
    }

    private fun resultRender(isSuccess: Boolean?, errorMessage: String?) {
        if (isSuccess != null) {
            // Открытие внешнего приложения
        }

        if (errorMessage != null) {
            setToast(errorMessage)
        }
    }

    private fun setState(state: AlbumState) {
        albumLiveData.postValue(state)
    }

    private fun setState(state: TracksState) {
        tracksLiveData.postValue(state)
    }

    private fun setState(state: TrackTriggerState) {
        showTrackTrigger.postValue(state)
    }

    fun setToast(message: String) {
        toastState.postValue(message)
    }
}