package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.search.SetTrack
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.util.consumer.ConsumerData
import java.util.concurrent.ExecutorService

class SearchInteractorImpl(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository,
    private val trackRepository: SetTrack,
    private val executor: ExecutorService
) : SearchInteractor {

    override fun searchTracks(expression: String, consumer: Consumer<ArrayList<Track>>) {
        executor.execute {
            val searchTracks = searchRepository.searchTracks(expression)
            when (searchTracks) {
                is Resource.Success -> consumer.consume(ConsumerData.Data(searchTracks.data))
                is Resource.Error -> consumer.consume(ConsumerData.Error(searchTracks.message))
            }
        }
    }

    override fun getHistory(consumer: Consumer<ArrayList<Track>>) {
        val historyTracks = historyRepository.getHistory()
        when (historyTracks) {
            is Resource.Success -> consumer.consume(ConsumerData.Data(historyTracks.data))
            is Resource.Error -> consumer.consume(ConsumerData.Error(historyTracks.message))
        }
    }

    override fun setHistory(track: Track) {
        historyRepository.setHistory(track)
    }

    override fun saveHistory() {
        historyRepository.saveHistory()
    }

    override fun clearHistory() {
        historyRepository.clearHistory()
    }

    override fun setTrack(track: Track): String = trackRepository.execute(track)
}