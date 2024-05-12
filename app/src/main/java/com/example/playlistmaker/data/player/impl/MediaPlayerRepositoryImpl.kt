package com.example.playlistmaker.data.player.impl

import com.example.playlistmaker.data.player.MediaPlayerRepository
import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.data.player.mapper.PlayerStatusMapper
import com.example.playlistmaker.data.player.mapper.PlayerTimeMapper
import com.example.playlistmaker.data.search.model.PlayerRequest
import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.consumer.Consumer

class MediaPlayerRepositoryImpl(
    private val playerClient: PlayerClient,
    private val playerTimeMapper: PlayerTimeMapper,
    private val playerStatusMapper: PlayerStatusMapper
) : MediaPlayerRepository {

    override fun preparePlayer(track: Track): Resource<PlayerStatus> {
        val playerResponse = playerClient.preparePlayer(PlayerRequest(track.previewUrl))
        return playerStatusMapper.map(playerResponse)
    }

    override fun startPlayer(): Resource<PlayerStatus> {
        val playerResponse = playerClient.start()
        return playerStatusMapper.map(playerResponse)
    }

    override fun pausePlayer(): Resource<PlayerStatus> {
        val playerResponse = playerClient.pause()
        return playerStatusMapper.map(playerResponse)
    }

    override fun setCompletionListener(consumer: Consumer<PlayerStatus>) {
        playerClient.setOnCompletionListener(consumer)
    }

    override fun getTimePlayer(): Resource<Int> {
        val playerResponse = playerClient.getCurrentPosition()
        return playerTimeMapper.map(playerResponse)
    }

    override fun clearPlayer() {
        playerClient.release()
    }
}