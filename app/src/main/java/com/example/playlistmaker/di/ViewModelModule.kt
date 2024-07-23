import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.data.converters.AlbumModelConverter
import com.example.playlistmaker.ui.media.favourites.view_model.FavouritesViewModel
import com.example.playlistmaker.ui.media.fragment.MediaPagerAdapter
import com.example.playlistmaker.ui.media.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.playlistadd.view_model.PlaylistAddViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    factory { (tabLayout: TabLayout, viewPager: ViewPager2) ->
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text =
                    androidContext().resources.getString(R.string.media_tab_favourites) //"Избранные треки"
                1 -> tab.text =
                    androidContext().resources.getString(R.string.media_tab_playlist) //"Плейлисты"
            }
        }
    }

    factory { (fragmentManager: FragmentManager, lifecycle: Lifecycle) ->
        MediaPagerAdapter(
            fragmentManager = fragmentManager,
            lifecycle = lifecycle
        )
    }

    // Search
    viewModel {
        SearchViewModel(
            application = androidApplication(),
            searchInteractor = get(),
            debounceUtils = get(),
        )
    }

    // Player
    viewModel { (trackId: Int, trackDataType: String) ->
        PlayerViewModel(
            application = androidApplication(),
            trackId = trackId,
            playerInteractor = get { parametersOf(trackDataType) },
            playlistInteractor = get()
        )
    }

    // Settings
    viewModel {
        SettingsViewModel(
            application = androidApplication(),
            sharingInteractor = get(),
            settingsInteractor = get()
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

    // AddAlbum
    viewModel {
        PlaylistAddViewModel(
            application = androidApplication(),
            playlistAddInteractor = get(),
            albumModelConverter = get()
        )
    }
}