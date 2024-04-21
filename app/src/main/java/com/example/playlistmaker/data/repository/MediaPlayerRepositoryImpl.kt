package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.PlayerRequest
import com.example.playlistmaker.data.mapper.PlayerStatusMapper
import com.example.playlistmaker.data.mapper.PlayerTimeMapper
import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.entity.PlayerStatus
import com.example.playlistmaker.domain.entity.Resource
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerRepositoryImpl(private val playerClient: PlayerClient) : MediaPlayerRepository {

    override fun preparePlayer(track: Track): Resource<PlayerStatus> {
        val playerResponse = playerClient.preparePlayer(PlayerRequest(track.previewUrl))
        return PlayerStatusMapper.map(playerResponse)
    }

    override fun startPlayer(): Resource<PlayerStatus> {
        val playerResponse = playerClient.start()
        return PlayerStatusMapper.map(playerResponse)
    }

    override fun pausePlayer(): Resource<PlayerStatus> {
        val playerResponse = playerClient.pause()
        return PlayerStatusMapper.map(playerResponse)
    }

    override fun setCompletionListener(consumer: Consumer<PlayerStatus>) {
        playerClient.setOnCompletionListener(consumer)
    }

    override fun getTimePlayer(): Resource<Int> {
        val playerResponse = playerClient.getCurrentPosition()
        return PlayerTimeMapper.map(playerResponse)
    }

    override fun clearPlayer() {
        playerClient.release()
    }
}