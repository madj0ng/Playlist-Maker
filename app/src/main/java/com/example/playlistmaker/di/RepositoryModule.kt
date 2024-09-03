import com.example.playlistmaker.creator.TYPE_ALBUM
import com.example.playlistmaker.creator.TYPE_FAVOURITES
import com.example.playlistmaker.creator.TYPE_HISTORY
import com.example.playlistmaker.creator.TYPE_SEARCH
import com.example.playlistmaker.data.album.AlbumRepositoryImpl
import com.example.playlistmaker.data.media.favourites.FavouritesRepositoryImpl
import com.example.playlistmaker.data.media.playlist.PlaylistRepositoryImpl
import com.example.playlistmaker.data.player.impl.GetTrackFromStringImpl
import com.example.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.player.mapper.PlayerStatusMapper
import com.example.playlistmaker.data.player.mapper.PlayerTimeMapper
import com.example.playlistmaker.data.playlistadd.impl.PlaylistAddRepositoryImpl
import com.example.playlistmaker.data.search.impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.example.playlistmaker.data.search.impl.SetTrackToStringImpl
import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.album.AlbumRepository
import com.example.playlistmaker.domain.media.favourites.FavouritesRepository
import com.example.playlistmaker.domain.media.playlist.PlaylistRepository
import com.example.playlistmaker.domain.player.GetTrackFromString
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.playlistadd.PlaylistAddRepository
import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.search.SetTrackToString
import com.example.playlistmaker.domain.settings.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    //getHistoryRepository
    single<HistoryRepository> {
        HistoryRepositoryImpl(
            trackMapper = get(),
            getTracks = get(named(TYPE_HISTORY)),
            setTrack = get(named(TYPE_HISTORY)),
            deleteTracks = get(named(TYPE_HISTORY))
        )
    }

    //SearchRepositoryImpl
    single<GetTrackFromString<TrackDto>> {
        GetTrackFromStringImpl(gson = get())
    }

    // getMediaPlayerRepository
    single<PlayerTimeMapper> {
        PlayerTimeMapper
    }

    single<PlayerStatusMapper> {
        PlayerStatusMapper
    }

    factory<PlayerRepository> { (trackDataType: String) ->
        PlayerRepositoryImpl(
            playerClient = get(),
            playerTimeMapper = get(),
            playerStatusMapper = get(),
            getTrackById = get(named(trackDataType)),
            getFavouriteTrackById = get(named(TYPE_FAVOURITES)),
            deleteFavouriteTracks = get(named(TYPE_FAVOURITES)),
            setFavouriteTrack = get(named(TYPE_FAVOURITES)),
            trackMapper = get(),
        )
    }

    //getSearchRepository
    single<SearchRepository> {
        SearchRepositoryImpl(
            networkClient = get(),
            trackMapper = get(),
            getTracks = get(named(TYPE_SEARCH)),
            setTracks = get(named(TYPE_SEARCH))
        )
    }

    //getTrackRepository
    single<SetTrackToString<TrackDto>> {
        SetTrackToStringImpl(gson = get())
    }

    // getSettingsRepository
    single<SettingsRepository> {
        SettingsRepositoryImpl(themeLocalStorage = get())
    }

    // Favourites
    single<FavouritesRepository> {
        FavouritesRepositoryImpl(
            trackSharedConvertor = get(),
            getTracks = get(named(TYPE_FAVOURITES))
        )
    }

    // Playlist
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            dataOfAlbum = get(named(TYPE_ALBUM)),
        )
    }

    // PlaylistAdd
    factory<PlaylistAddRepository> {
        PlaylistAddRepositoryImpl(
            dataOfAlbum = get(named(TYPE_ALBUM)),
            dataOfFile = get()
        )
    }

    factory<AlbumRepository>{
        AlbumRepositoryImpl(
            context = androidContext(),
            dataOfAlbum = get(named(TYPE_ALBUM)),
            albumExternalNavigator = get()
        )
    }
}