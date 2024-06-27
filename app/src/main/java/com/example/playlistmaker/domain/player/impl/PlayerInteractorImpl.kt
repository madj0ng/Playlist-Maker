package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.GetTrack
import com.example.playlistmaker.domain.player.MediaPlayerRepository
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerInteractorImpl(
    private val playerRepository: MediaPlayerRepository,
    private val trackRepository: GetTrack
) : PlayerInteractor {

    override fun loadTrackData(
        trackStr: String?,
        onComplete: (Track) -> Unit,
        onError: (String) -> Unit
    ) {
        val track = trackRepository.execute(trackStr)
        if (track != null) {
            onComplete.invoke(track)
        }
    }

    override fun preparePlayerSuspend(track: Track): Flow<PlayerStatus> {
        return playerRepository.preparePlayerSuspend(track).map { result ->
            when (result) {
                is Resource.Success -> {
                    PlayerStatus.Prepared()
                }

                is Resource.Error -> {
                    PlayerStatus.Default()
                }
            }
        }
    }

    override fun getPlayerStatus(): Flow<Boolean> {
        return playerRepository.getPlayerStatus().map { result ->
            when (result) {
                is Resource.Success -> {
                    result.data
                }

                is Resource.Error -> {
                    false
                }
            }
        }
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun setOnCompletionListenerSuspend(): Flow<PlayerStatus> {
        return playerRepository.setCompletionListenerSuspend().map { result ->
            when (result) {
                is Resource.Success -> {
                    PlayerStatus.Prepared()
                }

                is Resource.Error -> {
                    PlayerStatus.Prepared()
                }
            }
        }
    }

    override fun getTimePlayer(): Resource<Int> {
        return playerRepository.getTimePlayer()
    }

    override fun clearPlayer() {
        playerRepository.clearPlayer()
    }
}