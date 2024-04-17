package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.data.dto.PlayerRequest
import com.example.playlistmaker.data.dto.PlayerResponse
import com.example.playlistmaker.data.dto.PlayerStatusDto
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.entity.PlayerStatus

class MediaPlayerClient : PlayerClient {
    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(playerRequest: PlayerRequest): PlayerResponse<PlayerStatusDto> {
        mediaPlayer.setDataSource(playerRequest.previewUrl)
        mediaPlayer.prepare()
        return PlayerResponse.Data(PlayerStatusDto.PREPARED)
    }

    override fun start(): PlayerResponse<PlayerStatusDto> {
        mediaPlayer.start()
        return PlayerResponse.Data(PlayerStatusDto.PLAYING)
    }

    override fun pause(): PlayerResponse<PlayerStatusDto> {
        mediaPlayer.pause()
        return PlayerResponse.Data(PlayerStatusDto.PAUSED)
    }

    override fun setOnCompletionListener(consumer: Consumer<PlayerStatus>) {
        mediaPlayer.setOnCompletionListener {
            consumer.consume(ConsumerData.Data(PlayerStatus.PREPARED))
        }
    }

    override fun getCurrentPosition(): PlayerResponse<Int> {
        return PlayerResponse.Data(mediaPlayer.currentPosition)
    }

    override fun release() {
        mediaPlayer.release()
    }
}