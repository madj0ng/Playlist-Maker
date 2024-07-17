package com.example.playlistmaker.ui.player.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.activity.PlayerActivity.Companion.TRACK_START_TIME
import com.example.playlistmaker.ui.player.models.PlayerState
import com.example.playlistmaker.util.DebounceUtils.TIME_DEBOUNCE_DELAY
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    application: Application,
    trackId: Int,
    private val playerInteractor: PlayerInteractor,
) : AndroidViewModel(application) {

    private val screenLiveData = MutableLiveData<PlayerState>(PlayerState.Loading)
    private val statusLiveData = MutableLiveData<PlayerStatus>(PlayerStatus.Default())
    private val favouriteLiveData = MutableLiveData(false)
    private val toastLiveData = SingleLiveEvent<String>()

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

    override fun onCleared() {
        stopTimer()
        stopOnCompletionListener()

        // Освобождаем память от плеера
        playerInteractor.clearPlayer()
    }
}