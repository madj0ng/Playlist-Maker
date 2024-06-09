package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.models.PlayerState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.util.FormatUtils
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {
    companion object {
        // Округление в пикселях
        const val IMG_RADIUS_PX = 8F
        const val TRACK_KEY = "track_key"

        // Начальное значение трека
        const val TRACK_START_TIME = 0L

        fun createArgs(trackString: String): Bundle =
            bundleOf(TRACK_KEY to trackString)
    }

    private lateinit var viewModel: PlayerViewModel

    private lateinit var binding: ActivityPlayerBinding

    private val formatUtils: FormatUtils = getKoin().get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Распаковываем переданный класс
        val trackString = intent.getStringExtra(TRACK_KEY) ?: ""

        viewModel = getViewModel { parametersOf(trackString) }

        /*// Нажатие иконки назад экрана Настройки
        binding.back.setOnClickListener {
            super.finish()
        }*/

        // Подготовка плеера
        viewModel.preparePlayer()

        // Событие завершения проигрывания трека
        viewModel.onCompletePreparePlayer()

        // Действие при нажатии на ibPlay
        binding.ibPlay.setOnClickListener {
            viewModel.runPlayer()
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        onBackPressedDispatcher.onBackPressed()
    }

    private fun setCurrentTrackTime(time: Long) {
        binding.playTime.text = formatUtils.formatLongToTrakTime(time)
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
                    formatUtils.dpToPx(
                        IMG_RADIUS_PX,
                        binding.albumImage.context
                    )
                )
            )
            .into(binding.albumImage)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.collectionNameValue.text = track.collectionName
        binding.releaseDateValue.text = formatUtils.formatIsoToYear(track.releaseDate)
        binding.primaryGenreNameValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.trackTimeValue.text = formatUtils.formatLongToTrakTime(track.trackTimeMillis)

        // Уловия отображения
        binding.collectionNameGroup.isVisible = binding.collectionNameValue.text.isNotEmpty()
    }
}