package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.consumer.Consumer

interface MediaPlayerRepository {
    fun preparePlayer(track: Track): Resource<PlayerStatus>

    fun startPlayer(): Resource<PlayerStatus>

    fun pausePlayer(): Resource<PlayerStatus>

    fun setCompletionListener(consumer: Consumer<PlayerStatus>)

    fun getTimePlayer(): Resource<Int>

    fun clearPlayer()
}