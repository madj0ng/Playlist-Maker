package com.example.playlistmaker.creator

//object Creator {

//    fun provideSearchInteractor(context: Context): SearchInteractor {
//        return SearchInteractorImpl(
//            getSearchRepository(context),
//            getHistoryRepository(context),
//            getTrackRepository()
//        )
//    }

//    private fun getTrackRepository(): SetTrack {
//        return SetTrackToString()
//    }

//    private fun getSearchRepository(context: Context): SearchRepository {
//        return SearchRepositoryImpl(RetrofitNetworkClient(context))
//    }

//    private fun getHistoryRepository(context: Context): HistoryRepository {
//        return HistoryRepositoryImpl(
//            HistoryLocalStorage(
//                context.getSharedPreferences(
//                    SEARCH_PREFERENCES,
//                    Context.MODE_PRIVATE
//                )
//            )
//        )
//    }

//    // Плеер
//    fun providePlayerIneractor(): PlayerInteractor {
//        return PlayerInteractorImpl(getMediaPlayerRepository(), getTrackDetailRepository())
//    }

//    private fun getTrackDetailRepository(): GetTrack {
//        return GetTrackFromString()
//    }

//    private fun getMediaPlayerRepository(): MediaPlayerRepository {
//        return MediaPlayerRepositoryImpl(MediaPlayerClient())
//    }

//    // Настройки
//    fun providerSharingInteractor(context: Context): SharingInteractor {
//        return SharingInteractorImpl(ExternalNavigatorImpl(context))
//    }

//    fun providerSettingsInteractor(context: Context): SettingsInteractor {
//        return SettingsInteractorImpl(getSettingsRepository(context))
//    }

//    private fun getSettingsRepository(context: Context): SettingsRepository {
//        return SettingsRepositoryImpl(
//            ThemeLocalStorage(
//                context.getSharedPreferences(
//                    APP_PREFERENCES,
//                    Context.MODE_PRIVATE
//                )
//            )
//        )
//    }

//}