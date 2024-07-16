import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.creator.APP_PREFERENCES
import com.example.playlistmaker.creator.SEARCH_PREFERENCES
import com.example.playlistmaker.creator.TYPE_FAVOURITES
import com.example.playlistmaker.creator.TYPE_HISTORY
import com.example.playlistmaker.creator.TYPE_SEARCH
import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.data.player.audio.MediaPlayerClient
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.network.ItunesSearchApi
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.settings.impl.ThemeLocalStorage
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.storage.DeleteTrack
import com.example.playlistmaker.data.storage.DeleteTracks
import com.example.playlistmaker.data.storage.GetTrackById
import com.example.playlistmaker.data.storage.GetTracks
import com.example.playlistmaker.data.storage.SetTrack
import com.example.playlistmaker.data.storage.SetTracks
import com.example.playlistmaker.data.storage.db.AppDatabase
import com.example.playlistmaker.data.storage.db.DataOfFavourites
import com.example.playlistmaker.data.storage.empty.DataOfEmpty
import com.example.playlistmaker.data.storage.memory.DataOfSearch
import com.example.playlistmaker.data.storage.sharedpref.DataOfHistory
import com.example.playlistmaker.data.storage.sharedpref.LocalStoreImpl
import com.example.playlistmaker.data.storage.sharedpref.dao.LocalStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.binds
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

    // Инициализация базы данных
    single {
        AppDatabase.getInstance(androidContext())
    }

    // Данные для хранения результатов поиска
    single(named(TYPE_SEARCH)) {
        DataOfSearch()
    } binds (arrayOf(
        GetTracks::class,
        SetTracks::class,
        GetTrackById::class,
        DeleteTracks::class
    ))

    single(named(TYPE_HISTORY)) {
        DataOfHistory(
            localStore = get(),
            setTrackToString = get(),
            getTrackFromString = get()
        )
    } binds (arrayOf(
        GetTracks::class,
        SetTrack::class,
        GetTrackById::class,
        DeleteTracks::class
    ))

    single(named(TYPE_FAVOURITES)) {
        DataOfFavourites(
            appDatabase = get(),
            trackDbConvertor = get()
        )
    } binds (arrayOf(
        GetTracks::class,
        SetTrack::class,
        GetTrackById::class,
        DeleteTrack::class
    ))

    single(named("")) {
        DataOfEmpty()
    } binds (arrayOf(GetTrackById::class))

    single<LocalStorage> {
        LocalStoreImpl(
            sharedPreferences = get(named(SEARCH_PREFERENCES)),
        )
    }
}