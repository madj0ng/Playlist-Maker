package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.entity.PlayerStatus
import com.example.playlistmaker.domain.entity.Resource
import com.example.playlistmaker.domain.entity.Track

interface MediaPlayerRepository {
    fun preparePlayer(track: Track): Resource<PlayerStatus>

    fun startPlayer(): Resource<PlayerStatus>

    fun pausePlayer(): Resource<PlayerStatus>

    fun setCompletionListener(consumer: Consumer<PlayerStatus>)

    fun getTimePlayer(): Resource<Int>

    fun clearPlayer()
}