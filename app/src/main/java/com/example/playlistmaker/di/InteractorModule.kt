import com.example.playlistmaker.domain.media.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.media.favourites.impl.FavouritesInteractorImpl
import com.example.playlistmaker.domain.media.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.media.playlist.impl.PlaylistInteractorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val interactorModule = module {
    //providePlayerIneractor
    factory<PlayerInteractor> {
        PlayerInteractorImpl(
            playerRepository = get(),
            trackRepository = get()
        )
    }

    //provideSearchInteractor
    single<ExecutorService> {
        Executors.newCachedThreadPool()
    }

    single<SearchInteractor> {
        SearchInteractorImpl(
            searchRepository = get(),
            historyRepository = get(),
            trackRepository = get(),
        )
    }

    //providerSettingsInteractor
    single<SettingsInteractor> {
        SettingsInteractorImpl(settingsRepository = get())
    }

    //providerSharingInteractor
    single<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }

    // Favourites
    single<FavouritesInteractor> {
        FavouritesInteractorImpl(favouritesRepository = get())
    }

    // Playlist
    single<PlaylistInteractor> {
        PlaylistInteractorImpl(playlistRepository = get())
    }
}