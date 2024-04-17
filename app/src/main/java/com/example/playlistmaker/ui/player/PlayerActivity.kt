package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.entity.PlayerDetails
import com.example.playlistmaker.domain.entity.PlayerStatus
import com.example.playlistmaker.domain.entity.Resource
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.utils.FormatUtils
import com.example.playlistmaker.utils.HandlerUtils

class PlayerActivity : AppCompatActivity() {
    companion object {
        // Округление в пикселях
        const val IMG_RADIUS_PX = 8F
        const val TRACK_KEY = "track_key"

        // Начальное значение трека
        const val TRACK_START_TIME = 0L
    }

    private lateinit var binding: ActivityPlayerBinding

    // Статус плеера
    private var playerStatus = PlayerStatus.DEFAULT

    // Отложенная очередь задач
    private val handler = Handler(Looper.getMainLooper())
    private var trackRunnable = updateTrackTime()

    private val getPlayerIneractor = Creator.providePlayerIneractor()
    private var detailsRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Нажатие иконки назад экрана Настройки
        binding.back.setOnClickListener {
            super.finish()
        }

        // Распаковываем переданный класс
        val json = intent.getStringExtra(TRACK_KEY)

        // Подготовка плеера
        getPlayerIneractor.preparePlayer(
            trackStr = json,
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
            })

        // Событие завершения проигрывания трека
        getPlayerIneractor.setOnCompletionListener(consumer = object :
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
        })

        // Действие при нажатии на ibPlay
        binding.ibPlay.setOnClickListener {
            when (val resource = getPlayerIneractor.runPlayer(playerStatus)) {
                is Resource.Error -> {}
                is Resource.Success -> {
                    playerStatus = resource.data
                }
            }
            updateHandler(playerStatus)
            showPlayerState(playerStatus)
        }
    }

    override fun onPause() {
        super.onPause()

        // Пауза
        when (val resource = getPlayerIneractor.runPlayer(playerStatus)) {
            is Resource.Error -> {}
            is Resource.Success -> {
                playerStatus = resource.data
            }
        }
        showPlayerState(playerStatus)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Очистить очередь отложенных задач
        handler.removeCallbacksAndMessages(null)

        // Освобождаем память от плеера
        getPlayerIneractor.clearPlayer()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        onBackPressedDispatcher.onBackPressed()
    }

    private fun updateHandler(status: PlayerStatus) {
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
    }

    private fun setCurrentTrackTime(time: Long) {
        binding.playTime.text = FormatUtils.formatLongToTrakTime(time)
    }

    private fun fillPlayer(track: Track) {
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

    private fun showPlayerState(state: PlayerStatus) {
        when (state) {
            PlayerStatus.DEFAULT, PlayerStatus.PAUSED, PlayerStatus.PREPARED -> {
                binding.ibPlay.setImageResource(R.drawable.ic_play)
            }

            PlayerStatus.PLAYING -> {
                binding.ibPlay.setImageResource(R.drawable.ic_stop)
            }
        }
    }
}