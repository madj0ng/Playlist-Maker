package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.entity.Resource
import com.example.playlistmaker.domain.entity.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: Consumer<ArrayList<Track>>) {
        executor.execute {
            val searchTracks = tracksRepository.searchTracks(expression)
            when (searchTracks) {
                is Resource.Error -> {
                    consumer.consume(ConsumerData.Error(searchTracks.message))
                }

                is Resource.Success -> {
                    consumer.consume(ConsumerData.Data(searchTracks.data))
                }
            }
        }
    }

}