package com.example.playlistmaker.data.player

import com.example.playlistmaker.data.search.model.PlayerRequest
import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.data.search.model.PlayerStatusDto
import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.domain.search.model.PlayerStatus

interface PlayerClient {
    fun preparePlayer(playerRequest: PlayerRequest): PlayerResponse<PlayerStatusDto>

    fun start(): PlayerResponse<PlayerStatusDto>

    fun pause(): PlayerResponse<PlayerStatusDto>

    fun setOnCompletionListener(consumer: Consumer<PlayerStatus>)

    fun getCurrentPosition(): PlayerResponse<Int>

    fun release()
}