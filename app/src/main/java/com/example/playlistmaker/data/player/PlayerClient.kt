package com.example.playlistmaker.data.player

import com.example.playlistmaker.data.search.model.PlayerRequest
import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.data.search.model.PlayerStatusDto

interface PlayerClient {
    suspend fun preparePlayerSuspend(playerRequest: PlayerRequest): PlayerResponse<PlayerStatusDto>

    fun getPlayerStatus(): PlayerResponse<Boolean>

    fun start()

    fun pause()

    suspend fun setOnCompletionListenerSuspend(): PlayerResponse<PlayerStatusDto>

    fun getCurrentPosition(): PlayerResponse<Int>

    fun release()
}