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
            mediaPlayerRepository = get(),
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
            executor = get(),
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
}