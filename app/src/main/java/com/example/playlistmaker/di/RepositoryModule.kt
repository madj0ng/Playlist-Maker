import com.example.playlistmaker.data.media.favourites.FavouritesRepositoryImpl
import com.example.playlistmaker.data.media.playlist.PlaylistRepositoryImpl
import com.example.playlistmaker.data.player.impl.GetTrackFromString
import com.example.playlistmaker.data.player.impl.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.player.mapper.PlayerStatusMapper
import com.example.playlistmaker.data.player.mapper.PlayerTimeMapper
import com.example.playlistmaker.data.search.impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.example.playlistmaker.data.search.impl.SetTrackToString
import com.example.playlistmaker.data.search.mapper.TrackMapper
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.media.favourites.FavouritesRepository
import com.example.playlistmaker.domain.media.playlist.PlaylistRepository
import com.example.playlistmaker.domain.player.GetTrack
import com.example.playlistmaker.domain.player.MediaPlayerRepository
import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.search.SetTrack
import com.example.playlistmaker.domain.settings.SettingsRepository
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
    single<PlayerTimeMapper> {
        PlayerTimeMapper
    }

    single<PlayerStatusMapper> {
        PlayerStatusMapper
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(
            playerClient = get(),
            playerTimeMapper = get(),
            playerStatusMapper = get(),
        )
    }

    //getSearchRepository
    single<TrackMapper> {
        TrackMapper
    }

    single<SearchRepository> {
        SearchRepositoryImpl(
            networkClient = get(),
            trackMapper = get()
        )
    }

    //getTrackRepository
    single<SetTrack> {
        SetTrackToString(gson = get())
    }

    // getSettingsRepository
    single<SettingsRepository> {
        SettingsRepositoryImpl(themeLocalStorage = get())
    }

    // Favourites
    single<FavouritesRepository> {
        FavouritesRepositoryImpl()
    }

    // Playlist
    single<PlaylistRepository> {
        PlaylistRepositoryImpl()
    }
}