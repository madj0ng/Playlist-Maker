package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository,
) : SearchInteractor {

    override fun searchTracks(expression: String): Flow<Pair<ArrayList<Track>?, String?>> {
        return searchRepository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Error -> {
                    Pair(null, result.message)
                }

                is Resource.Success -> {
                    Pair(result.data, null)
                }
            }
        }
    }

    override suspend fun getHistory(): Flow<Pair<ArrayList<Track>?, String?>> {
        return historyRepository.getHistory().map { result ->
            when (result) {
                is Resource.Error -> {
                    Pair(null, result.message)
                }

                is Resource.Success -> {
                    Pair(result.data, null)
                }
            }
        }
    }

    override suspend fun setHistory(track: Track) {
        historyRepository.setHistory(track)
    }

    override suspend fun clearHistory() {
        historyRepository.clearHistory()
    }
}