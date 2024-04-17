package com.example.playlistmaker.data.player

import com.example.playlistmaker.data.dto.PlayerRequest
import com.example.playlistmaker.data.dto.PlayerResponse
import com.example.playlistmaker.data.dto.PlayerStatusDto
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.entity.PlayerStatus

interface PlayerClient {
    fun preparePlayer(playerRequest: PlayerRequest): PlayerResponse<PlayerStatusDto>

    fun start(): PlayerResponse<PlayerStatusDto>

    fun pause(): PlayerResponse<PlayerStatusDto>

    fun setOnCompletionListener(consumer: Consumer<PlayerStatus>)

    fun getCurrentPosition(): PlayerResponse<Int>

    fun release()
}