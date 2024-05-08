package com.example.playlistmaker.domain.search

import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.domain.search.model.Track

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: Consumer<ArrayList<Track>>)
    fun setTrack(track: Track): String
    fun getHistory(consumer: Consumer<ArrayList<Track>>)
    fun setHistory(track: Track)
    fun saveHistory()
    fun clearHistory()
}