import com.example.playlistmaker.data.player.GetTrack
import com.example.playlistmaker.data.player.MediaPlayerRepository
import com.example.playlistmaker.data.player.impl.GetTrackFromString
import com.example.playlistmaker.data.player.impl.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.search.HistoryRepository
import com.example.playlistmaker.data.search.SearchRepository
import com.example.playlistmaker.data.search.SetTrack
import com.example.playlistmaker.data.search.impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.example.playlistmaker.data.search.impl.SetTrackToString
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    //getHistoryRepository
    single<HistoryRepository> {
        HistoryRepositoryImpl(
            searchHistory = get(),
            setTrackRepository = get(),
            getTrackRepository = get()
        )
    }

    //SearchRepositoryImpl
    single<GetTrack> {
        GetTrackFromString(gson = get())
    }

    // getMediaPlayerRepository
    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(playerClient = get())
    }

    //getSearchRepository
    single<SearchRepository> {
        SearchRepositoryImpl(networkClient = get())
    }

    //getTrackRepository
    single<SetTrack> {
        SetTrackToString(gson = get())
    }

    // getSettingsRepository
    single<SettingsRepository> {
        SettingsRepositoryImpl(themeLocalStorage = get())
    }
}