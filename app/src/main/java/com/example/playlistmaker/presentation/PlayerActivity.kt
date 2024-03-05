package com.example.playlistmaker.presentation

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.search.Track
import com.example.playlistmaker.utils.FormatUtils
import com.example.playlistmaker.utils.HandlerUtils
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {
    companion object {
        // Округление в пикселях
        const val IMG_RADIUS_PX = 8F
        const val TRACK_KEY = "track_key"
    }

    private lateinit var binding: ActivityPlayerBinding

    // Плеер
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT

    // Отложенная очередь задач
    private var handler: Handler? = null
    private var trackRunnable = updateTrackTime()

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        handler = Handler(Looper.getMainLooper())

        // Нажатие иконки назад экрана Настройки
        binding.back.setOnClickListener {
            super.finish()
        }

        // Распаковываем переданный класс
        val json = intent.getStringExtra(TRACK_KEY)
        if (json != null) {
            track = Gson().fromJson(json, Track::class.java)
            fillPlayer(track)
        }

        // Подготовка плеера
        preparePlayer()

        // Действие при нажатии на ibPlay
        binding.ibPlay.setOnClickListener {
            controlPlayerState()
        }
    }

    override fun onPause() {
        super.onPause()

        // Пауза
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Очистить очередь отложенных задач
        handler?.removeCallbacksAndMessages(null)

        // Освобождаем память от плеера
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        if (track.previewUrl != null) {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()
        }
        mediaPlayer.setOnPreparedListener {
            showPlayerState(PlayerState.PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            showPlayerState(PlayerState.PREPARED)
            setCurrentTrackTime(0L)
            handler?.removeCallbacks(trackRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        handler?.post(trackRunnable)
        showPlayerState(PlayerState.PLAYING)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        handler?.removeCallbacks(trackRunnable)
        showPlayerState(PlayerState.PAUSED)
    }

    private fun updateTrackTime(): Runnable {
        return object : Runnable {
            override fun run() {
                // Обновляем время

                setCurrentTrackTime(mediaPlayer.currentPosition.toLong())
                // И снова планируем то же действие через 0.3 секунд
                handler?.postDelayed(
                    this,
                    HandlerUtils.TIME_DEBOUNCE_DELAY,
                )
            }
        }
    }

    private fun setCurrentTrackTime(time: Long) {
        binding.playTime.text = FormatUtils.formatLongToTrakTime(time)
    }

    private fun controlPlayerState() {
        when (playerState) {
            PlayerState.DEFAULT -> {}

            PlayerState.PAUSED, PlayerState.PREPARED -> {
                startPlayer()
            }

            PlayerState.PLAYING -> {
                pausePlayer()
            }
        }
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
        binding.collectionNameGroup.isVisible = track.collectionName.isNotEmpty()
    }

    private fun showPlayerState(state: PlayerState) {
        playerState = state
        when (state) {
            PlayerState.DEFAULT, PlayerState.PAUSED, PlayerState.PREPARED -> {
                binding.ibPlay.setImageResource(R.drawable.ic_play)
            }

            PlayerState.PLAYING -> {
                binding.ibPlay.setImageResource(R.drawable.ic_stop)
            }
        }
    }
}