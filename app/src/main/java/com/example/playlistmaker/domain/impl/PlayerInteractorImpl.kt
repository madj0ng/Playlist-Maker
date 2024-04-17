package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.entity.PlayerDetails
import com.example.playlistmaker.domain.entity.PlayerStatus
import com.example.playlistmaker.domain.entity.Resource
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.domain.repository.TrackDetailRepository
import java.util.concurrent.Executors

class PlayerInteractorImpl(
    private val mediaPlayerRepository: MediaPlayerRepository,
    private val trackRepository: TrackDetailRepository
) : PlayerInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun preparePlayer(
        trackStr: String?,
        consumer: Consumer<PlayerDetails>
    ) {
        executor.execute {
            val track = trackRepository.getTrackByString(trackStr)
            if (track == null) {
                consumer.consume(ConsumerData.Error("Ошибка получения трека"))
            } else {
                when (val playerStatus = mediaPlayerRepository.preparePlayer(track)) {
                    is Resource.Success -> {
                        val playerDetails = PlayerDetails(
                            track = track,
                            status = playerStatus.data
                        )
                        consumer.consume(ConsumerData.Data(playerDetails))
                    }

                    is Resource.Error -> {
                        val playerDetails = PlayerDetails(
                            track = track,
                            status = PlayerStatus.DEFAULT
                        )
                        consumer.consume(ConsumerData.Data(playerDetails))
                    }
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

    override fun startPlayer(): Resource<PlayerStatus>{
        return mediaPlayerRepository.startPlayer()
    }

    override fun pausePlayer(): Resource<PlayerStatus>{
        return mediaPlayerRepository.pausePlayer()
    }

    override fun setOnCompletionListener(consumer: Consumer<PlayerStatus>) {
        mediaPlayerRepository.setCompletionListener(consumer)
    }

    override fun getTimePlayer(): Resource<Int>{
        return mediaPlayerRepository.getTimePlayer()
    }

    override fun clearPlayer() {
        mediaPlayerRepository.clearPlayer()
    }
}