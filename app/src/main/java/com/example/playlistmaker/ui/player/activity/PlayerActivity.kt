package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.models.PlayerState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.util.FormatUtils

class PlayerActivity : AppCompatActivity() {
    companion object {
        // Округление в пикселях
        const val IMG_RADIUS_PX = 8F
        const val TRACK_KEY = "track_key"

        // Начальное значение трека
        const val TRACK_START_TIME = 0L
    }

    private lateinit var viewModel: PlayerViewModel

    private lateinit var binding: ActivityPlayerBinding

//    // Статус плеера
//    private var playerStatus = PlayerStatus.DEFAULT

    // Отложенная очередь задач
//    private val handler = Handler(Looper.getMainLooper())
//    private var trackRunnable = updateTrackTime()

//    private val getPlayerIneractor = Creator.providePlayerIneractor()
//    private var detailsRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Распаковываем переданный класс
        val trackString = intent.getStringExtra(TRACK_KEY) ?: ""

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(trackString)
        )[PlayerViewModel::class.java]

        // Нажатие иконки назад экрана Настройки
        binding.back.setOnClickListener {
            super.finish()
        }

        // Подготовка плеера
        viewModel.preparePlayer()
        /*getPlayerIneractor.preparePlayer(
            trackStr = trackString,
            consumer = object : Consumer<PlayerDetails> {
                override fun consume(data: ConsumerData<PlayerDetails>) {
                    if (detailsRunnable != null) {
                        handler.removeCallbacks(detailsRunnable!!)
                    }
                    val newDetailsRunnable = Runnable {
                        when (data) {
                            is ConsumerData.Error -> showError(data.message)
                            is ConsumerData.Data -> {
                                val playerDetails = data.value
                                playerStatus = playerDetails.status
                                fillPlayer(playerDetails.track)
                            }
                        }
                    }
                    handler.post(newDetailsRunnable)
                    detailsRunnable = newDetailsRunnable
                }
            })*/

        // Событие завершения проигрывания трека
        viewModel.onCompletePreparePlayer()
        /*getPlayerIneractor.setOnCompletionListener(consumer = object :
            Consumer<PlayerStatus> {
            override fun consume(data: ConsumerData<PlayerStatus>) {
                when (data) {
                    is ConsumerData.Error -> {}
                    is ConsumerData.Data -> {
                        playerStatus = data.value
                        showPlayerState(playerStatus)
                        setCurrentTrackTime(TRACK_START_TIME)
                        handler.removeCallbacks(trackRunnable)
                    }
                }
            }
        })*/

        // Действие при нажатии на ibPlay
        binding.ibPlay.setOnClickListener {
            viewModel.runPlayer()
            /*when (val resource = getPlayerIneractor.runPlayer(playerStatus)) {
                is Resource.Error -> {}
                is Resource.Success -> {
                    playerStatus = resource.data
                }
            }
            updateHandler(playerStatus)
            showPlayerState(playerStatus)*/
        }

        viewModel.observeScreenState().observe(this) {
            render(it)
        }

        viewModel.observePlayerStatus().observe(this) {
            setPlayerStatus(it)
        }

        viewModel.observeTrackTime().observe(this) {
            setCurrentTrackTime(it)
        }

        viewModel.observeToastState().observe(this) {
            showToast(it)
        }
    }

    private fun setPlayerStatus(status: PlayerStatus) {
        when (status) {
            PlayerStatus.DEFAULT -> showPlayerDefault()
            PlayerStatus.PREPARED -> showPlayerPrepared()
            PlayerStatus.PLAYING -> showPlayerPlaying()
            PlayerStatus.PAUSED -> showPlayerPaused()
        }
    }

    private fun showPlayerPlaying() {
        binding.ibPlay.setImageResource(R.drawable.ic_stop)
    }

    private fun showPlayerDefault() {
        binding.ibPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPlayerPrepared() {
        binding.ibPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPlayerPaused() {
        binding.ibPlay.setImageResource(R.drawable.ic_play)
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Content -> showContent(state.track)
            is PlayerState.Empty -> showEmpty()
            is PlayerState.Error -> showErr()
            is PlayerState.Loading -> showLoading()
        }
    }

    private fun showContent(track: Track) {
        fillTrack(track)

        fillImage()
    }

    private fun showEmpty() {}

    private fun showErr() {}

    private fun showLoading() {}

    override fun onPause() {
        super.onPause()

        // Пауза
        viewModel.pausePlayer()
    }

    /*override fun onDestroy() {
        super.onDestroy()

        // Очистить очередь отложенных задач
        handler.removeCallbacksAndMessages(null)

        // Освобождаем память от плеера
        getPlayerIneractor.clearPlayer()
    }*/

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        onBackPressedDispatcher.onBackPressed()
    }

    /*private fun updateHandler(status: PlayerStatus) {
        when (status) {
            PlayerStatus.PLAYING -> {
                handler.post(trackRunnable)
            }

            PlayerStatus.PAUSED -> {
                handler.removeCallbacks(trackRunnable)
            }

            PlayerStatus.PREPARED, PlayerStatus.DEFAULT -> {}
        }
    }

    private fun updateTrackTime(): Runnable {
        return object : Runnable {
            override fun run() {
                // Обновляем время
                when (val resource = getPlayerIneractor.getTimePlayer()) {
                    is Resource.Error -> {}
                    is Resource.Success -> {
                        setCurrentTrackTime(resource.data.toLong())
                    }
                }
                // И снова планируем то же действие через 0.3 секунд
                handler.postDelayed(
                    this,
                    HandlerUtils.TIME_DEBOUNCE_DELAY,
                )
            }
        }
    }*/

    private fun setCurrentTrackTime(time: Long) {
        binding.playTime.text = FormatUtils.formatLongToTrakTime(time)
    }

    private fun fillImage() {
        binding.playlistAdd.setImageResource(R.drawable.ic_add_playlist)
        binding.likedAdd.setImageResource(R.drawable.ic_add_liked)
    }

    private fun fillTrack(track: Track) {
        // Заполнение
        Glide.with(binding.albumImage)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    FormatUtils.dpToPx(
                        IMG_RADIUS_PX,
                        binding.albumImage.context
                    )
                )
            )
            .into(binding.albumImage)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.collectionNameValue.text = track.collectionName
        binding.releaseDateValue.text = FormatUtils.formatIsoToYear(track.releaseDate)
        binding.primaryGenreNameValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.trackTimeValue.text = FormatUtils.formatLongToTrakTime(track.trackTimeMillis)

        // Уловия отображения
        binding.collectionNameGroup.isVisible = binding.collectionNameValue.text.isNotEmpty()
    }
}