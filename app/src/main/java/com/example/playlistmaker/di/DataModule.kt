import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.creator.APP_PREFERENCES
import com.example.playlistmaker.creator.SEARCH_PREFERENCES
import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.data.player.audio.MediaPlayerClient
import com.example.playlistmaker.data.search.LocalStorage
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.network.ItunesSearchApi
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.storage.HistoryLocalStorage
import com.example.playlistmaker.data.settings.impl.ThemeLocalStorage
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    val iTunesSearchUrl = "https://itunes.apple.com"

    // getMediaPlayerRepository
    factory {
        MediaPlayer()
    }

    factory<PlayerClient> {
        MediaPlayerClient(get())
    }

    //getTrackDetailRepository
    factory {
        Gson()
    }

    //getHistoryRepository
    factory<SharedPreferences>(named(SEARCH_PREFERENCES)) {
        androidContext().getSharedPreferences(
            SEARCH_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    single<LocalStorage> {
        HistoryLocalStorage(sharedPreferences = get(named(SEARCH_PREFERENCES)))
    }

    //getSettingsRepository
    factory<SharedPreferences>(named(APP_PREFERENCES)) {
        androidContext().getSharedPreferences(
            APP_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    single {
        ThemeLocalStorage(sharedPreferences = get(named(APP_PREFERENCES)))
    }

    // getSearchRepository
    single<ItunesSearchApi> {
        Retrofit.Builder()
            .baseUrl(iTunesSearchUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesSearchApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            context = androidContext(),
            itunesSearchService = get()
        )
    }

    // providerSharingInteractor
    factory { Intent() }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(
            context = androidContext(),
            intent = get()
        )
    }
}