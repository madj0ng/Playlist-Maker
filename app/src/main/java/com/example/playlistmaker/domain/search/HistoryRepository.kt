package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(): Flow<Resource<ArrayList<Track>>>
    suspend fun setHistory(track: Track)
    suspend fun clearHistory()
}