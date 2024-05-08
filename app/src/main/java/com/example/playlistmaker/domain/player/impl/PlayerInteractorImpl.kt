package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.util.consumer.ConsumerData
import com.example.playlistmaker.data.player.GetTrack
import com.example.playlistmaker.data.player.MediaPlayerRepository
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import java.util.concurrent.Executors

class PlayerInteractorImpl(
    private val mediaPlayerRepository: MediaPlayerRepository,
    private val trackRepository: GetTrack
) : PlayerInteractor {
    private val executor = Executors.newCachedThreadPool()

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

    override fun preparePlayer(
        track: Track,
        consumer: Consumer<PlayerStatus>
    ) {
        executor.execute {
            when (val playerStatus = mediaPlayerRepository.preparePlayer(track)) {
                is Resource.Success -> {
                    consumer.consume(ConsumerData.Data(playerStatus.data))
                }

                is Resource.Error -> {
                    consumer.consume(ConsumerData.Data(PlayerStatus.DEFAULT))
                }
            }
        }
    }

    override fun runPlayer(status: PlayerStatus): Resource<PlayerStatus> {
        return when (status) {
            PlayerStatus.DEFAULT -> {
                Resource.Success(status)
            }

            PlayerStatus.PAUSED, PlayerStatus.PREPARED -> {
                startPlayer()
            }

            PlayerStatus.PLAYING -> {
                pausePlayer()
            }
        }
    }

    override fun startPlayer(): Resource<PlayerStatus> {
        return mediaPlayerRepository.startPlayer()
    }

    override fun pausePlayer(): Resource<PlayerStatus> {
        return mediaPlayerRepository.pausePlayer()
    }

    override fun setOnCompletionListener(consumer: Consumer<PlayerStatus>) {
        mediaPlayerRepository.setCompletionListener(consumer)
    }

    override fun getTimePlayer(): Resource<Int> {
        return mediaPlayerRepository.getTimePlayer()
    }

    override fun clearPlayer() {
        mediaPlayerRepository.clearPlayer()
    }
}