package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(expression: String): Flow<Pair<ArrayList<Track>?, String?>>

    //    fun setTrack(track: Track): String
    suspend fun getHistory(): Flow<Pair<ArrayList<Track>?, String?>>

    suspend fun setHistory(track: Track)

    //    fun saveHistory()
    suspend fun clearHistory()
}