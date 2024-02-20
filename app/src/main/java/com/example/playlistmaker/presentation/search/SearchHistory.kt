package com.example.playlistmaker.presentation.search

import android.content.SharedPreferences
import com.google.gson.Gson

interface Observer {
    fun addToList(track: Track)
}

interface Observable {
    fun addSubscriber(observer: Observer)
    fun notifySubscriber(track: Track)
}

class SearchHistory(
    private val sharedPref: SharedPreferences,
    private val historyAdapter: TrackAdapter
) : Observer {

    companion object {
        const val HISTORY_LIST_KEY = "history_list_key"
    }

    fun setHistory() {
        if (!read().isNullOrEmpty()) {
            historyAdapter.tracks.addAll(read()!!.toCollection(ArrayList()))
        }
    }

    fun saveHistory() {
        write(historyAdapter.tracks.toTypedArray())
    }

    // чтение
    private fun read(): Array<Track>? {
        val tracksStr = sharedPref.getString(HISTORY_LIST_KEY, null) ?: return emptyArray()
        return Gson().fromJson(tracksStr, Array<Track>::class.java)
    }

    // запись
    private fun write(tracks: Array<Track>) {
        val json = Gson().toJson(tracks)
        sharedPref.edit()
            .putString(HISTORY_LIST_KEY, json)
            .apply()
    }

    override fun addToList(track: Track) {
        val maxSize = 9
        val startPosition = 0
        var i = startPosition + 1

        // Добавляем новый трек в историю
        historyAdapter.tracks.add(startPosition, track)
        historyAdapter.notifyItemInserted(startPosition)
        historyAdapter.notifyItemRangeChanged(startPosition, historyAdapter.tracks.size)

        while (i < historyAdapter.tracks.size) {
            // Удаляем трек, если он уже есть в истории и его индекс больше 9
            if (i > maxSize || historyAdapter.tracks[i].trackId == track.trackId) {
                historyAdapter.tracks.removeAt(i)
                historyAdapter.notifyItemRemoved(i)
                historyAdapter.notifyItemRangeChanged(i, historyAdapter.tracks.size)
                i--
            }
            i++
        }
    }
}