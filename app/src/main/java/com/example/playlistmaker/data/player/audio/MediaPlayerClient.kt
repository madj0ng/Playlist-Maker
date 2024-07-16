package com.example.playlistmaker.data.player.audio

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.data.search.model.PlayerRequest
import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.data.search.model.PlayerStatusDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class MediaPlayerClient(private var mediaPlayer: MediaPlayer) : PlayerClient {
    override suspend fun preparePlayerSuspend(playerRequest: PlayerRequest): PlayerResponse<PlayerStatusDto> {
        return withContext(Dispatchers.IO) {
            try {
                mediaPlayer.setDataSource(playerRequest.previewUrl)
                mediaPlayer.prepare()
                PlayerResponse.Data(PlayerStatusDto.PREPARED)
            } catch (e: Throwable) {
                PlayerResponse.Data(PlayerStatusDto.DEFAULT)
            }
        }
    }

    override fun getPlayerStatus(): PlayerResponse<Boolean> {
        return PlayerResponse.Data(mediaPlayer.isPlaying)
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override suspend fun setOnCompletionListenerSuspend(): PlayerResponse<PlayerStatusDto> =
        suspendCancellableCoroutine { continuation ->
            mediaPlayer.setOnCompletionListener {
                if (continuation.isActive) {
                    continuation.resume(PlayerResponse.Data(PlayerStatusDto.PREPARED))
                }
            }
        }

    override fun getCurrentPosition(): PlayerResponse<Int> {
        return PlayerResponse.Data(mediaPlayer.currentPosition)
    }

    override fun release() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}