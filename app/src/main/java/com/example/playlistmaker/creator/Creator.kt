package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.player.MediaPlayerClient
import com.example.playlistmaker.data.repository.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.TrackDetailRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.repository.TrackDetailRepository

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksIneractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    // Плеер
    fun providePlayerIneractor(): PlayerInteractor {
        return PlayerInteractorImpl(getMediaPlayerRepository(), getTrackDetailRepository())
    }

    private fun getTrackDetailRepository(): TrackDetailRepository {
        return TrackDetailRepositoryImpl()
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl(MediaPlayerClient())
    }
}