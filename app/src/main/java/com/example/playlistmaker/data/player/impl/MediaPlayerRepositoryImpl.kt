package com.example.playlistmaker.data.player.impl

import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.data.player.mapper.PlayerStatusMapper
import com.example.playlistmaker.data.player.mapper.PlayerTimeMapper
import com.example.playlistmaker.data.search.model.PlayerRequest
import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.domain.player.MediaPlayerRepository
import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MediaPlayerRepositoryImpl(
    private val playerClient: PlayerClient,
    private val playerTimeMapper: PlayerTimeMapper,
    private val playerStatusMapper: PlayerStatusMapper
) : MediaPlayerRepository {

    override fun preparePlayerSuspend(track: Track): Flow<Resource<PlayerStatus>> = flow {
        when (val playerResponse =
            playerClient.preparePlayerSuspend(PlayerRequest(track.previewUrl))) {
            is PlayerResponse.Data -> emit(playerStatusMapper.map(playerResponse))
        }
    }

    override fun getPlayerStatus(): Flow<Resource<Boolean>> = flow {
        when (val playerResponse = playerClient.getPlayerStatus()) {
            is PlayerResponse.Data -> emit(Resource.Success(playerResponse.value))
        }
    }

    override fun startPlayer() {
        playerClient.start()
    }

    override fun pausePlayer() {
        playerClient.pause()
    }

    override fun setCompletionListenerSuspend(): Flow<Resource<PlayerStatus>> = flow {
        when (val playerResponse = playerClient.setOnCompletionListenerSuspend()) {
            is PlayerResponse.Data -> emit(playerStatusMapper.map(playerResponse))
        }
    }

    override fun getTimePlayer(): Resource<Int> {
        val playerResponse = playerClient.getCurrentPosition()
        return playerTimeMapper.map(playerResponse)
    }

    override fun clearPlayer() {
        playerClient.release()
    }
}