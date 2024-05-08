package com.example.playlistmaker.domain.player

import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.domain.search.model.Track

interface PlayerInteractor {
    fun loadTrackData(
        trackStr: String?,
        onComplete: (Track) -> Unit,
        onError: (String) -> Unit
    )

    fun preparePlayer(
        track: Track,
        consumer: Consumer<PlayerStatus>
    )

    fun runPlayer(status: PlayerStatus): Resource<PlayerStatus>

    fun startPlayer(): Resource<PlayerStatus>

    fun pausePlayer(): Resource<PlayerStatus>

    fun getTimePlayer(): Resource<Int>

    fun clearPlayer()

    fun setOnCompletionListener(consumer: Consumer<PlayerStatus>)
}