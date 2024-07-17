package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface PlayerInteractor {
    fun loadTrackData(trackId: Int): Flow<Pair<Track?, String?>>

    fun preparePlayerSuspend(track: Track): Flow<PlayerStatus>

    fun onFavouritePressed(track: Track): Flow<Boolean>

    fun getPlayerStatus(): Flow<Boolean>

    fun startPlayer()

    fun pausePlayer()

    fun getTimePlayer(): Resource<Int>

    fun clearPlayer()

    fun setOnCompletionListenerSuspend(): Flow<PlayerStatus>
}