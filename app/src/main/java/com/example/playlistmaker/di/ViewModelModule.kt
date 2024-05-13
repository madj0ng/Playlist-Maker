import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.example.playlistmaker.creator.HISTORY_ADAPTER
import com.example.playlistmaker.creator.SEARCH_ADAPTER
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.activity.SearchAdapter
import com.example.playlistmaker.ui.search.activity.SearchViewHolder
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.util.HandlerUtils
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named
import org.koin.dsl.module
import android.view.ViewGroup
import org.koin.core.parameter.parametersOf

val viewModelModule = module {
    single {
        Handler(Looper.getMainLooper())
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
}