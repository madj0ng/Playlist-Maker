package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.playlist.models.PlaylistState
import com.example.playlistmaker.ui.player.models.PlayerState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.playlistadd.models.AlbumTriggerState
import com.example.playlistmaker.util.DebounceUtils
import com.example.playlistmaker.util.FormatUtils
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    companion object {
        // Округление в пикселях
        const val IMG_RADIUS_PX = 8F
        const val TRACK_KEY = "track_key"
        const val TRACK_DATA_TYPE = "track_data_type"

        // Начальное значение трека
        const val TRACK_START_TIME = 0L

        fun createArgs(trackId: Int, trackDataType: String): Bundle =
            bundleOf(
                TRACK_KEY to trackId,
                TRACK_DATA_TYPE to trackDataType
            )
    }

    private lateinit var viewModel: PlayerViewModel

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val formatUtils: FormatUtils = getKoin().get()

    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var onPlayerTrackClickDebounce: (Album) -> Unit

    // bottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Распаковываем переданный класс
        val trackId = requireArguments().getInt(TRACK_KEY)
        val trackDataType = requireArguments().getString(TRACK_DATA_TYPE, "")

        viewModel = getViewModel { parametersOf(trackId, trackDataType) }

        bottomSheetBehavior = BottomSheetBehavior
            .from(binding.standardBottomSheet)
            .apply { state = BottomSheetBehavior.STATE_HIDDEN }

        onPlayerTrackClickDebounce = debounce(
            DebounceUtils.CLICK_DEBOUNCE_DELAY,
            lifecycleScope,
            true
        ) { album ->
            viewModel.addTrackToAlbum(album)
        }
        playerAdapter = PlayerAdapter { album -> onPlayerTrackClickDebounce(album) }

        binding.rvBottomSheetPlaylist.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        // Нажатие иконки назад
        binding.back.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Действие при нажатии на ibPlay
        binding.ibPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        // Действие при нажатии на понравившиеся
        binding.ibFavourite.setOnClickListener {
            viewModel.onFavouriteButtonClicked()
        }

        binding.playlistAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        viewModel.loadAlbumsData()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        // Переход на плейлист
        binding.btPlaylistAdd.setOnClickListener {
            render(AlbumTriggerState.Player)
        }

        viewModel.observeScreenState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observePlayerStatus().observe(viewLifecycleOwner) {
            setPlayerStatus(it)
        }

        viewModel.observeToastState().observe(viewLifecycleOwner) {
            showToast(it)
        }
        viewModel.observeFavourite().observe(viewLifecycleOwner) {
            setFavoutite(it)
        }
        viewModel.observeAlbums().observe(viewLifecycleOwner) {
            setAlbums(it)
        }
        viewModel.observeBottomSheet().observe(viewLifecycleOwner) {
            bottomSheetBehavior.state = it
        }
    }

    private fun setPlayerStatus(status: PlayerStatus) {
        when (status) {
            is PlayerStatus.Default -> showPlayerDefault(status.progress)
            is PlayerStatus.Prepared -> showPlayerPrepared(status.progress)
            is PlayerStatus.Playing -> showPlayerPlaying(status.progress)
            is PlayerStatus.Paused -> showPlayerPaused(status.progress)
        }
    }

    private fun showPlayerPlaying(time: Long) {
        setCurrentTrackTime(time)
        binding.ibPlay.setImageResource(R.drawable.ic_stop)
    }

    private fun showPlayerDefault(time: Long) {
        setCurrentTrackTime(time)
        binding.ibPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPlayerPrepared(time: Long) {
        setCurrentTrackTime(time)
        binding.ibPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPlayerPaused(time: Long) {
        setCurrentTrackTime(time)
        binding.ibPlay.setImageResource(R.drawable.ic_play)
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Content -> showContent(state.track)
            is PlayerState.Empty -> {}
            is PlayerState.Error -> {}
            is PlayerState.Loading -> {}
        }
    }

    private fun showContent(track: Track) {
        fillTrack(track)

        fillImage()
    }

    override fun onPause() {
        super.onPause()

        // Пауза
        viewModel.stopPlayer()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setFavoutite(isFavourite: Boolean) {
        if (isFavourite) {
            binding.ibFavourite.setImageResource(R.drawable.ic_is_liked)
        } else {
            binding.ibFavourite.setImageResource(R.drawable.ic_add_liked)
        }
    }

    private fun setCurrentTrackTime(time: Long) {
        binding.playTime.text = formatUtils.formatLongToTrakTime(time)
    }

    private fun fillImage() {
        binding.playlistAdd.setImageResource(R.drawable.ic_add_playlist)
    }

    private fun setAlbums(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> {
                playerAdapter.setList(state.data)
                binding.rvBottomSheetPlaylist.adapter = playerAdapter
                playerAdapter.notifyItemChanged(1)
            }

            is PlaylistState.Empty -> {}
            is PlaylistState.Error -> {}
            PlaylistState.Loading -> {}
        }
    }

    private fun fillTrack(track: Track) {
        // Заполнение
        Glide.with(binding.albumImage)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    formatUtils.dpToPx(
                        IMG_RADIUS_PX,
                        binding.albumImage.context
                    )
                )
            )
            .into(binding.albumImage)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.collectionNameValue.text = track.collectionName
        binding.releaseDateValue.text = formatUtils.formatIsoToYear(track.releaseDate)
        binding.primaryGenreNameValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.trackTimeValue.text = formatUtils.formatLongToTrakTime(track.trackTimeMillis)

        // Уловия отображения
        binding.collectionNameGroup.isVisible = binding.collectionNameValue.text.isNotEmpty()
    }

    private fun render(state: AlbumTriggerState) {
        when (state) {
            is AlbumTriggerState.Player -> navigateToAdd()
            else -> {}
        }
    }

    private fun navigateToAdd() {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(
            R.id.action_playerFragment_to_playlistAddFragment,
        )
    }
}