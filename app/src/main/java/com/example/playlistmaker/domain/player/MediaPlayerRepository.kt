package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface MediaPlayerRepository {
    fun preparePlayerSuspend(track: Track): Flow<Resource<PlayerStatus>>

    fun getPlayerStatus(): Flow<Resource<Boolean>>

    fun startPlayer()

    fun pausePlayer()

    fun setCompletionListenerSuspend(): Flow<Resource<PlayerStatus>>

    fun getTimePlayer(): Resource<Int>

    fun clearPlayer()
}