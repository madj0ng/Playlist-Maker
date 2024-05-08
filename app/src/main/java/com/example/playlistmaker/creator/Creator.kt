package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.player.GetTrack
import com.example.playlistmaker.data.player.MediaPlayerRepository
import com.example.playlistmaker.data.player.audio.MediaPlayerClient
import com.example.playlistmaker.data.player.impl.GetTrackFromString
import com.example.playlistmaker.data.player.impl.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.search.HistoryRepository
import com.example.playlistmaker.data.search.SearchRepository
import com.example.playlistmaker.data.search.SetTrack
import com.example.playlistmaker.data.search.impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.example.playlistmaker.data.search.impl.SetTrackToString
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.storage.HistoryLocalStorage
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.settings.impl.ThemeLocalStorage
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(
            getSearchRepository(context),
            getHistoryRepository(context),
            getTrackRepository()
        )
    }

    private fun getTrackRepository(): SetTrack {
        return SetTrackToString()
    }

    private fun getSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient(context))
    }

    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(
            HistoryLocalStorage(
                context.getSharedPreferences(
                    SEARCH_PREFERENCES,
                    Context.MODE_PRIVATE
                )
            )
        )
    }

    // Плеер
    fun providePlayerIneractor(): PlayerInteractor {
        return PlayerInteractorImpl(getMediaPlayerRepository(), getTrackDetailRepository())
    }

    private fun getTrackDetailRepository(): GetTrack {
        return GetTrackFromString()
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl(MediaPlayerClient())
    }

    // Настройки
    fun providerSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }

    fun providerSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            ThemeLocalStorage(
                context.getSharedPreferences(
                    APP_PREFERENCES,
                    Context.MODE_PRIVATE
                )
            )
        )
    }

}