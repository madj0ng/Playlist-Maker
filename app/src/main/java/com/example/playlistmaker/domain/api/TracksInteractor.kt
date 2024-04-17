package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.entity.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer<ArrayList<Track>>)

//    interface TracksConsumer{
//        fun consume(foundTracks: ArrayList<Track>)
//    }
}