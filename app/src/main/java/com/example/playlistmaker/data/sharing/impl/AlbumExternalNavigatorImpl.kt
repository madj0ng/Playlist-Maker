package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.AlbumExternalNavigator
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.FormatUtils

class AlbumExternalNavigatorImpl(
    private val context: Context,
    private val intent: Intent,
    private val formatUtils: FormatUtils
) : AlbumExternalNavigator {

    private companion object {
        const val STR_NEWLINE = "\n"
        const val STR_SPACE = " "
        const val STR_POINT = ". "
        const val STR_DASH = " - "
        const val STR_SQUOTES = "("
        const val STR_EQUOTES = ")"
    }

    override fun shareLink(album: Album, tracks: List<Track>): Boolean {
        // Поделиться приложением
        val type = context.getString(R.string.sharing_type)
        intent.setAction(Intent.ACTION_SEND)
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, getShareAppLink(album, tracks))
            setType(type)
        }
        return startIntent(intent)
    }

    private fun getShareAppLink(album: Album, tracks: List<Track>): String {
        val stringList = mutableListOf<String>()
        // Заголовок плейлиста
        stringList.add(album.name)
        stringList.add(if (album.description.isEmpty()) "" else album.description)
        stringList.add(
            formatUtils.formatIntToTrackCount(album.tracksCount) + STR_SPACE + context.getString(
                R.string.playlist_item
            )
        )
        // Таблица
        tracks.forEachIndexed { index, track ->
            stringList.add(
                index.toString() +
                        STR_POINT +
                        track.artistName +
                        STR_DASH +
                        track.trackName +
                        STR_SPACE +
                        STR_SQUOTES +
                        formatUtils.formatLongToTrakTime(track.trackTimeMillis) +
                        STR_EQUOTES
            )
        }
        return stringList.joinToString(separator = STR_NEWLINE)
    }

    private fun startIntent(intent: Intent): Boolean {
        // ошибка в случае "ActivityNotFoundException"
        return try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (err: Exception) {
            false
        }
    }
}