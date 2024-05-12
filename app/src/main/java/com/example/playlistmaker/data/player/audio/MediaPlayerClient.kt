package com.example.playlistmaker.data.player.audio

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.data.search.model.PlayerRequest
import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.data.search.model.PlayerStatusDto
import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.util.consumer.ConsumerData

class MediaPlayerClient(private var mediaPlayer: MediaPlayer) : PlayerClient {
//    private var mediaPlayer = MediaPlayer()

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