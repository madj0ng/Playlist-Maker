package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.entity.PlayerDetails
import com.example.playlistmaker.domain.entity.PlayerStatus
import com.example.playlistmaker.domain.entity.Resource

interface PlayerInteractor {
    fun preparePlayer(
        trackStr: String?,
        consumer: Consumer<PlayerDetails>
    )

    fun runPlayer(status: PlayerStatus): Resource<PlayerStatus>

    fun startPlayer(): Resource<PlayerStatus>

    fun pausePlayer(): Resource<PlayerStatus>

    fun getTimePlayer(): Resource<Int>

    fun clearPlayer()

    fun setOnCompletionListener(consumer: Consumer<PlayerStatus>)
}