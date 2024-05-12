package com.example.playlistmaker.ui.player.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.activity.PlayerActivity.Companion.TRACK_START_TIME
import com.example.playlistmaker.ui.player.models.PlayerState
import com.example.playlistmaker.util.HandlerUtils
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.SingleLiveEvent
import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.util.consumer.ConsumerData

class PlayerViewModel(
    application: Application,
    trackString: String?,
    private val playerInteractor: PlayerInteractor,
    private val handler: Handler,
    private val handlerUtils: HandlerUtils
) : AndroidViewModel(application) {

    private val screenLiveData = MutableLiveData<PlayerState>(PlayerState.Loading)
    private val statusLiveData = MutableLiveData<PlayerStatus>(PlayerStatus.DEFAULT)
    private val trackTimeLiveData = MutableLiveData<Long>(TRACK_START_TIME)
    private val toastLiveData = SingleLiveEvent<String>()

    init {
        playerInteractor.loadTrackData(
            trackStr = trackString,
            onComplete = { track ->
                this.track = track
                renderState(PlayerState.Content(track))
            },
            onError = {
                showToast(it)
            }
        )
    }

    private lateinit var track: Track

    companion object {
        private val TRACK_TIME_TOKEN = Any()

//        fun getViewModelFactory(trackString: String?): ViewModelProvider.Factory =
//            viewModelFactory {
//                initializer {
//                    PlayerViewModel(
//                        this[APPLICATION_KEY] as Application,
//                        trackString,
//                        Creator.providePlayerIneractor(),
//                    )
//                }
//            }
    }

//    private val handler = Handler(Looper.getMainLooper())

    fun observeScreenState(): LiveData<PlayerState> = screenLiveData
    fun observePlayerStatus(): LiveData<PlayerStatus> = statusLiveData
    fun observeTrackTime(): LiveData<Long> = trackTimeLiveData
    fun observeToastState(): LiveData<String> = toastLiveData

    // Подготовка плеера
    fun preparePlayer() {
        playerInteractor.preparePlayer(
            track = this.track,
            consumer = object : Consumer<PlayerStatus> {
                override fun consume(data: ConsumerData<PlayerStatus>) {
                    when (data) {
                        is ConsumerData.Data -> {
                            setStatus(data.value)
                        }

                        is ConsumerData.Error -> {
                            showToast(data.message)
                        }
                    }
                }
            }
        )
    }

    // Завершение проигрывания трека
    fun onCompletePreparePlayer() {
        playerInteractor.setOnCompletionListener(consumer = object :
            Consumer<PlayerStatus> {
            override fun consume(data: ConsumerData<PlayerStatus>) {
                when (data) {
                    is ConsumerData.Data -> {
                        // Завершаем задачу получения времени проигрываемого трека
                        stopTrackTime()

                        // Актуализируем сатус проигрывателя
                        setStatus(data.value)

                        // Обнуляем время
                        setTrackTime(TRACK_START_TIME)
                    }

                    is ConsumerData.Error -> {
                        showToast(data.message)
                    }
                }
            }
        })
    }

    fun runPlayer() {
        val resource = when (statusLiveData.value ?: PlayerStatus.DEFAULT) {
            PlayerStatus.DEFAULT -> {
                Resource.Success(PlayerStatus.DEFAULT)
            }

            PlayerStatus.PAUSED, PlayerStatus.PREPARED -> {
                startTrackTime()
                playerInteractor.startPlayer()
            }

            PlayerStatus.PLAYING -> {
                stopTrackTime()
                playerInteractor.pausePlayer()
            }
        }

        when (resource) {
            is Resource.Success -> {
                setStatus(resource.data)
            }

            is Resource.Error -> {
                showToast(resource.message)
            }
        }
    }

    fun pausePlayer() {
        val resource = when (val status = statusLiveData.value ?: PlayerStatus.DEFAULT) {
            PlayerStatus.DEFAULT, PlayerStatus.PAUSED, PlayerStatus.PREPARED -> {
                Resource.Success(status)
            }

            PlayerStatus.PLAYING -> {
                stopTrackTime()
                playerInteractor.pausePlayer()
            }
        }

        when (resource) {
            is Resource.Success -> {
                setStatus(resource.data)
            }

            is Resource.Error -> {
                showToast(resource.message)
            }
        }
    }

    private fun startTrackTime() {
        stopTrackTime()

        val trackTimeRunnable = updateTrackTimePlayer()
        handler.post(trackTimeRunnable)
    }

    private fun stopTrackTime() {
        handler.removeCallbacksAndMessages(TRACK_TIME_TOKEN)
    }

    private fun updateTrackTimePlayer(): Runnable {
        return object : Runnable {
            override fun run() {
                // Обновляем время
                when (val resource = playerInteractor.getTimePlayer()) {
                    is Resource.Error -> {}
                    is Resource.Success -> {
                        setTrackTime(resource.data.toLong())
                    }
                }
                // И снова планируем то же действие через 0.3 секунд
                val postTime = SystemClock.uptimeMillis() + handlerUtils.TIME_DEBOUNCE_DELAY
                handler.postAtTime(
                    this,
                    TRACK_TIME_TOKEN,
                    postTime,
                )
            }
        }
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

    private fun setTrackTime(trackTime: Long) {
        trackTimeLiveData.postValue(trackTime)
    }

    override fun onCleared() {
        // Очистить очередь от задачи
        handler.removeCallbacksAndMessages(TRACK_TIME_TOKEN)

        // Освобождаем память от плеера
        playerInteractor.clearPlayer()
    }
}