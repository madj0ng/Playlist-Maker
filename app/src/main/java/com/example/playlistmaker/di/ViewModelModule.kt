import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.HISTORY_ADAPTER
import com.example.playlistmaker.creator.SEARCH_ADAPTER
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.activity.MediaPagerAdapter
import com.example.playlistmaker.ui.media.favourites.view_model.FavouritesViewModel
import com.example.playlistmaker.ui.media.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.activity.SearchAdapter
import com.example.playlistmaker.ui.search.activity.SearchViewHolder
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.util.HandlerUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    single {
        Handler(Looper.getMainLooper())
    }

    single { (tabLayout: TabLayout, viewPager: ViewPager2) ->
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text =
                    androidContext().resources.getString(R.string.media_tab_favourites) //"Избранные треки"
                1 -> tab.text =
                    androidContext().resources.getString(R.string.media_tab_playlist) //"Плейлисты"
            }
        }
    }

    // Adapters
    single<SearchAdapter.SearchClickListener>(named(SEARCH_ADAPTER)) {
        object : SearchAdapter.SearchClickListener, KoinComponent {
            override fun onTrackClick(track: Track) {
                val handler: Handler = getKoin().get()
                val handlerUtils: HandlerUtils = getKoin().get()
                val viewModel: SearchViewModel = getKoin().get()
                if (handlerUtils.clickDebounce(handler)) {
                    viewModel.setHistory(track)
                    viewModel.startActiviryPlayer(track)
                }
            }
        }
    }

    single(named(SEARCH_ADAPTER)) {
        SearchAdapter(clickListener = get(named(SEARCH_ADAPTER)))
    }

    single<SearchAdapter.SearchClickListener>(named(HISTORY_ADAPTER)) {
        object : SearchAdapter.SearchClickListener, KoinComponent {
            override fun onTrackClick(track: Track) {
                val handler: Handler = getKoin().get()
                val handlerUtils: HandlerUtils = getKoin().get()
                val viewModel: SearchViewModel = getKoin().get()
                if (handlerUtils.clickDebounce(handler)) {
                    viewModel.setHistory(track)
                    viewModel.startActiviryPlayer(track)
                }
            }
        }
    }

    single(named(HISTORY_ADAPTER)) {
        SearchAdapter(clickListener = get(named(HISTORY_ADAPTER)))
    }

    single { (fragmentManager: FragmentManager, lifecycle: Lifecycle) ->
        MediaPagerAdapter(
            fragmentManager = fragmentManager,
            lifecycle = lifecycle
        )
    }

    // ViewHolder
    factory<LayoutInflater> { (parent: ViewGroup) ->
        LayoutInflater.from(parent.context)
    }

    factory<TrackViewBinding> { (parent: ViewGroup) ->
        TrackViewBinding.inflate(get { parametersOf(parent) }, parent, false)
    }

    factory<SearchViewHolder> { (parent: ViewGroup, clickListener: SearchAdapter.SearchClickListener) ->
        SearchViewHolder(
            binding = get { parametersOf(parent) },
            clickListener = clickListener,
            formatUtils = get()
        )
    }

    // Search
    viewModel {
        SearchViewModel(
            application = androidApplication(),
            searchInteractor = get(),
            handler = get(),
            handlerUtils = get(),
            intent = get(),
        )
    }

    // Player
    viewModel { (trackString: String?) ->
        PlayerViewModel(
            application = androidApplication(),
            trackString = trackString,
            playerInteractor = get(),
            handler = get(),
            handlerUtils = get(),
        )
    }

    // Settings
    viewModel {
        SettingsViewModel(
            application = androidApplication(),
            sharingInteractor = get(),
            settingsInteractor = get(),
        )
    }

    // Media
    // Playlist
    viewModel {
        PlaylistViewModel(playlistInteractor = get())
    }

    // Favourites
    viewModel {
        FavouritesViewModel(favouritesInteractor = get())
    }
}