import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.creator.APP_PREFERENCES
import com.example.playlistmaker.creator.SEARCH_PREFERENCES
import com.example.playlistmaker.creator.TYPE_ALBUM
import com.example.playlistmaker.creator.TYPE_FAVOURITES
import com.example.playlistmaker.creator.TYPE_HISTORY
import com.example.playlistmaker.creator.TYPE_SEARCH
import com.example.playlistmaker.data.converters.FavouriteTrackDbConvertor
import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.data.player.audio.MediaPlayerClient
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.network.ItunesSearchApi
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.settings.impl.ThemeLocalStorage
import com.example.playlistmaker.data.sharing.AlbumExternalNavigator
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.AlbumExternalNavigatorImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.storage.DeleteItem
import com.example.playlistmaker.data.storage.DeleteTracks
import com.example.playlistmaker.data.storage.GetItemById
import com.example.playlistmaker.data.storage.GetItems
import com.example.playlistmaker.data.storage.SetItem
import com.example.playlistmaker.data.storage.SetTracks
import com.example.playlistmaker.data.storage.db.AppDatabase
import com.example.playlistmaker.data.storage.db.DataOfAlbum
import com.example.playlistmaker.data.storage.db.FavouritesLocalDataSource
import com.example.playlistmaker.data.storage.empty.DataOfEmpty
import com.example.playlistmaker.data.storage.file.DataOfFile
import com.example.playlistmaker.data.storage.memory.DataOfSearch
import com.example.playlistmaker.data.storage.sharedpref.DataOfHistory
import com.example.playlistmaker.data.storage.sharedpref.LocalStoreImpl
import com.example.playlistmaker.data.storage.sharedpref.dao.LocalStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidApplication
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

    factory<AlbumExternalNavigator> {
        AlbumExternalNavigatorImpl(
            context = androidContext(),
            intent = get(),
            formatUtils = get()
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
        GetItems::class,
        SetTracks::class,
        GetItemById::class,
        DeleteTracks::class
    ))

    single(named(TYPE_HISTORY)) {
        DataOfHistory(
            localStore = get(),
            setTrackToString = get(),
            getTrackFromString = get()
        )
    } binds (arrayOf(
        GetItems::class,
        SetItem::class,
        GetItemById::class,
        DeleteTracks::class
    ))

    single(named(TYPE_FAVOURITES)) {
        FavouritesLocalDataSource(
            appDatabase = get(),
            trackDbConvertor = get()
        )
    } binds (arrayOf(
        GetItems::class,
        SetItem::class,
        GetItemById::class,
        DeleteItem::class
    ))

    factory(named(TYPE_ALBUM)) {
        DataOfAlbum(
            appDatabase = get(),
            albumModelConverter = get(),
            trackDbConverter = get()
        )
    } binds (arrayOf(
        GetItemById::class
    ))

    factory {
        DataOfFile(
            application = androidApplication(),
        )
    }

    // База данных
    single {
        FavouriteTrackDbConvertor()
    }

    single(named("")) {
        DataOfEmpty()
    } binds (arrayOf(GetItemById::class))

    single<LocalStorage> {
        LocalStoreImpl(
            sharedPreferences = get(named(SEARCH_PREFERENCES)),
        )
    }

//    single {
//        DataOfTrack(
//            appDatabase = get(),
//            trackDbConvertor = get()
//        )
//    }
}