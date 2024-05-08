package com.example.playlistmaker.data.search

import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.domain.search.model.Track

interface HistoryRepository {
    fun getHistory(): Resource<ArrayList<Track>>
    fun setHistory(track: Track)
    fun saveHistory()
    fun clearHistory()
}