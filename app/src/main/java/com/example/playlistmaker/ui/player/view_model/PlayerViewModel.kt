package com.example.playlistmaker.ui.player.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.media.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.playlist.models.PlaylistState
import com.example.playlistmaker.ui.player.fragment.PlayerFragment.Companion.TRACK_START_TIME
import com.example.playlistmaker.ui.player.models.PlayerState
import com.example.playlistmaker.util.DebounceUtils.TIME_DEBOUNCE_DELAY
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.stringReplace
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val application: Application,
    trackId: Int,
    private val playerInteractor: PlayerInteractor,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {

    private val screenLiveData = MutableLiveData<PlayerState>(PlayerState.Loading)
    private val statusLiveData = MutableLiveData<PlayerStatus>(PlayerStatus.Default())
    private val favouriteLiveData = MutableLiveData(false)
    private val toastLiveData = MutableLiveData<String>()
    private val albumsLiveData = MutableLiveData<PlaylistState>(PlaylistState.Loading)
    private val dialogLiveData = MutableLiveData<String>()
    private val bottomSheetLiveData = MutableLiveData<Int>()

    private var timerJob: Job? = null
    private var onCompletionListenerJob: Job? = null

    init {
        viewModelScope.launch {
            playerInteractor
                .loadTrackData(trackId)
                .collect { pair ->
                    loadResult(pair.first, pair.second)
                }
        }
    }

    private lateinit var track: Track

    fun observeScreenState(): LiveData<PlayerState> = screenLiveData
    fun observePlayerStatus(): LiveData<PlayerStatus> = statusLiveData
    fun observeToastState(): LiveData<String> = toastLiveData
    fun observeFavourite(): LiveData<Boolean> = favouriteLiveData
    fun observeAlbums(): LiveData<PlaylistState> = albumsLiveData
    fun observeBottomSheet(): LiveData<Int> = bottomSheetLiveData
    fun observeDialog(): LiveData<String> = dialogLiveData

    private fun loadResult(track: Track?, errorMessage: String?) {
        if (track != null) {
            this.track = track

            renderState(PlayerState.Content(track))
            renderFavourite(track.isFavourite)

            // Подготовка плеера
            initMediaPlayer(track)
        }

        if (errorMessage != null) {
            showToast(errorMessage)
        }
    }

    fun setBottomSheetState(message: String) {
        dialogLiveData.postValue(message)
    }

    fun setDialogState(state: Int) {
        bottomSheetLiveData.postValue(state)
    }

    fun addTrackToAlbum(album: Album) {
        var findId: Int? = null
        val track = when (screenLiveData.value) {
            is PlayerState.Content -> (screenLiveData.value as PlayerState.Content).track

            else -> null
        }
        if (track != null) {
            findId = album.tracksId.find { it == track.trackId }
            if (findId == null) {
                viewModelScope.launch {
                    playlistInteractor
                        .addTrackToAlbum(track, album)
                        .collect { resId ->
                            stringReplace(application, resId, album.name)?.let {
                                setDialogState(BottomSheetBehavior.STATE_HIDDEN)
                                setBottomSheetState(it)
                            }
                        }
                }
            } else {
                stringReplace(application, R.string.playlistadd_also_add_track, album.name)?.let {
                    setDialogState(BottomSheetBehavior.STATE_COLLAPSED)
                    setBottomSheetState(it)
                }
            }
        }
    }

    fun loadAlbumsData() {
        viewModelScope.launch {
            playlistInteractor
                .loadFavourites()
                .collect { pair ->
                    loadResult(pair.first, pair.second)
                }
        }
    }

    // Подготовка плеера
    private fun initMediaPlayer(track: Track) {
        // Подготовка плеера
        viewModelScope.launch {
            playerInteractor
                .preparePlayerSuspend(track)
                .collect { setStatus(it) }
        }
    }

    fun onPlayButtonClicked() {
        when (statusLiveData.value) {
            is PlayerStatus.Playing -> {
                pausePlayer()
            }

            is PlayerStatus.Paused, is PlayerStatus.Prepared -> {
                startPlayer()
            }

            else -> {}
        }
    }

    fun onFavouriteButtonClicked() {
        viewModelScope.launch {
            playerInteractor
                .onFavouritePressed(track)
                .collect {
                    renderFavourite(it)
                }
        }
    }

    fun stopPlayer() {
        when (statusLiveData.value) {
            is PlayerStatus.Playing -> {
                pausePlayer()
            }

            else -> {}
        }
    }

    // Завершение проигрывания трека
    private fun startOnCompletionListenerSuspend() {
        onCompletionListenerJob = viewModelScope.launch {
            playerInteractor
                .setOnCompletionListenerSuspend()
                .collect {
                    stopTimer()
                    setStatus(it)
                }
        }
    }

    private fun stopOnCompletionListener() {
        onCompletionListenerJob?.cancel()
    }

    private fun getCurrentPlayerPosition(): Long {
        return when (val resource = playerInteractor.getTimePlayer()) {
            is Resource.Error -> {
                TRACK_START_TIME
            }

            is Resource.Success -> {
                resource.data.toLong()
            }
        }
    }

    private fun startPlayer() {
        startOnCompletionListenerSuspend()

        playerInteractor.startPlayer()
        setStatus(PlayerStatus.Playing(getCurrentPlayerPosition()))

        startTimer()
    }

    private fun pausePlayer() {
        stopOnCompletionListener()

        playerInteractor.pausePlayer()
        setStatus(PlayerStatus.Paused(getCurrentPlayerPosition()))

        stopTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            var isPosition = true
            while (isPosition) {
                delay(TIME_DEBOUNCE_DELAY)
                setStatus(PlayerStatus.Playing(getCurrentPlayerPosition()))
                playerInteractor.getPlayerStatus().collect { isPosition = it }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun showToast(message: String) {
        toastLiveData.postValue(message)
    }

    private fun setStatus(status: PlayerStatus) {
        statusLiveData.postValue(status)
    }

    private fun renderState(state: PlayerState) {
        screenLiveData.postValue(state)
    }

    private fun renderFavourite(isFavourite: Boolean) {
        favouriteLiveData.postValue(isFavourite)
    }

    private fun loadResult(dbalbums: List<Album>?, errorMessage: String?) {
        val albums = mutableListOf<Album>()
        if (dbalbums != null) {
            albums.clear()
            albums.addAll(dbalbums)
        }
        when {
            errorMessage != null -> {
                albumsLiveData.postValue(PlaylistState.Error(errorMessage))
            }

            (albums.isNotEmpty()) -> {
                albumsLiveData.postValue(PlaylistState.Content(albums))
            }

            else -> {
                albumsLiveData.postValue(PlaylistState.Empty(""))
            }
        }
    }

    override fun onCleared() {
        stopTimer()
        stopOnCompletionListener()

        // Освобождаем память от плеера
        playerInteractor.clearPlayer()
    }
}