package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository,
) : PlayerInteractor {

    override fun loadTrackData(trackId: Int): Flow<Pair<Track?, String?>> {
        return playerRepository.getTrackById(trackId).map { result ->
            when (result) {
                is Resource.Error -> {
                    Pair(null, result.message)
                }

                is Resource.Success -> {
                    Pair(result.data, null)
                }
            }
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

    override fun onFavouritePressed(track: Track): Flow<Boolean> {
        return playerRepository.onFavouritePressed(track)
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