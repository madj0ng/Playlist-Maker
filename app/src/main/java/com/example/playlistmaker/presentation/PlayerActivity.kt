package com.example.playlistmaker.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.search.Track
import com.example.playlistmaker.utils.FormatUtils
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {
    companion object {
        // Округление в пикселях
        const val IMG_RADIUS_PX = 8F
        const val TRACK_KEY = "track_key"
    }

    private val back: ImageView by lazy { findViewById(R.id.back) }
    private val albumImage: ImageView by lazy { findViewById(R.id.albumImage) }
    private val trackName: TextView by lazy { findViewById(R.id.trackName) }
    private val artistName: TextView by lazy { findViewById(R.id.artistName) }
    private val collectionName: TextView by lazy { findViewById(R.id.collectionNameValue) }
    private val releaseDate: TextView by lazy { findViewById(R.id.releaseDateValue) }
    private val primaryGenreName: TextView by lazy { findViewById(R.id.primaryGenreNameValue) }
    private val country: TextView by lazy { findViewById(R.id.countryValue) }
    private val trackTime: TextView by lazy { findViewById(R.id.trackTimeValue) }
    private val collectionNameGroup: Group by lazy { findViewById(R.id.collectionNameGroup) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Нажатие иконки назад экрана Настройки
        back.setOnClickListener {
            super.finish()
        }

        // Распаковываем переданный класс
        val json = intent.getStringExtra(TRACK_KEY)
        if (json != null) {
            fillPlayer(Gson().fromJson(json, Track::class.java))
        }
    }

    private fun fillPlayer(track: Track) {
        // Заполнение
        Glide.with(albumImage)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    FormatUtils.dpToPx(
                        IMG_RADIUS_PX,
                        albumImage.context
                    )
                )
            )
            .into(albumImage)
        trackName.text = track.trackName
        artistName.text = track.artistName
        collectionName.text = track.collectionName
        releaseDate.text = FormatUtils.formatIsoToYear(track.releaseDate)
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        trackTime.text = FormatUtils.formatLongToTrakTime(track.trackTimeMillis)

        // Уловия отображения
        collectionNameGroup.isVisible = track.collectionName.isNotEmpty()
    }
}