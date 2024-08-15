package com.example.playlistmaker.ui.album.fragment

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.TYPE_ALBUM
import com.example.playlistmaker.databinding.FragmentAlbumBinding
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.album.model.AlbumState
import com.example.playlistmaker.ui.album.model.ListItemChangeState
import com.example.playlistmaker.ui.album.model.TracksState
import com.example.playlistmaker.ui.album.view_model.AlbumViewModel
import com.example.playlistmaker.ui.media.favourites.fragment.TracksAdapter
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.playlistedit.fragment.PlaylistEditFragment
import com.example.playlistmaker.ui.search.models.TrackTriggerState
import com.example.playlistmaker.util.DebounceUtils
import com.example.playlistmaker.util.FormatUtils
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AlbumFragment : Fragment() {
    companion object {
        const val ALBUM_KEY = "album_key"

        fun createArgs(albumId: Long): Bundle =
            bundleOf(
                ALBUM_KEY to albumId
            )
    }

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val formatUtils: FormatUtils = getKoin().get()
    private lateinit var viewModel: AlbumViewModel

    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var onAlbumTrackClickDebounce: (Track) -> Unit

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var deleteDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val albumId = requireArguments().getLong(ALBUM_KEY)

        viewModel = getViewModel { parametersOf(albumId) }

        // Переход на прослушивание трека
        onAlbumTrackClickDebounce = debounce(
            DebounceUtils.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { track ->
            viewModel.startActiviryPlayer(track, TYPE_ALBUM)
        }

        // Растягиваемый список треков
        bottomSheetBehavior = BottomSheetBehavior
            .from(binding.incPlaylistBootomSheet.playlistBottomSheet)
            .apply { state = BottomSheetBehavior.STATE_COLLAPSED }

        // Растягиваемый список меню
        menuBottomSheetBehavior = BottomSheetBehavior
            .from(binding.incMenuBootomSheet.menuBottomSheet)
            .apply { state = BottomSheetBehavior.STATE_HIDDEN }

        // Диалог
        deleteDialog = MaterialAlertDialogBuilder(requireActivity())

        // Адаптер списка треков
        tracksAdapter = TracksAdapter(
            { track ->
                onAlbumTrackClickDebounce(track)
            },
            { listItem ->
                showTrackDeleteDialog(listItem)
                true
            }
        )
        binding.incPlaylistBootomSheet.rvBottomSheetPlaylist.adapter = tracksAdapter
        binding.incPlaylistBootomSheet.rvBottomSheetPlaylist.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        // Назад
        binding.ibBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Меню
        binding.ibMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Поделиться
        binding.ibShare.setOnClickListener {
            viewModel.shareApp()
        }

        // Поделиться
        binding.incMenuBootomSheet.tvButtonShare.setOnClickListener {
            viewModel.shareApp()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // Редактирование плейлиста
        binding.incMenuBootomSheet.tvButtonEdit.setOnClickListener {
            viewModel.editAlbum()
        }

        // Удаление плейлиста
        binding.incMenuBootomSheet.tvButtonDelete.setOnClickListener {
            viewModel.deleteAlbumDialog()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        viewModel.observeTracks().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeAlbum().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowTrackTrigger().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeItemChange().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeDialog().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeBackPress().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowEdit().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observerToast().observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun render(state: AlbumState) {
        when (state) {
            is AlbumState.Content -> showAlbumContent(state.data)
            is AlbumState.Empty -> {}
            is AlbumState.Error -> {}
            is AlbumState.Loading -> {}
        }
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Content -> showTracksContent(state.data)
            is TracksState.Empty -> {}
            is TracksState.Error -> {}
            is TracksState.Loading -> {}
        }
    }

    private fun render(state: TrackTriggerState) {
        when (state) {
            is TrackTriggerState.Album -> navigateToPlayer(state.trackId, state.dataType)
            else -> {}
        }
    }

    private fun render(state: ListItemChangeState) {
        tracksAdapter.deleteTrack(state.position)
        tracksAdapter.notifyItemRemoved(state.position)
        tracksAdapter.notifyItemRangeChanged(state.rangeStart, state.rangeEnd)
    }

    private fun render(message: String) {
        showAlbumDeleteDialog(message)
    }

    private fun render(isBackPress: Boolean) {
        if (isBackPress) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun render(albumId: Long) {
        navigateToPlaylistEdit(albumId)
    }

    private fun showAlbumContent(album: Album) {
        // Заполнение детального описания
        fillBody(album)

        // Заполнение детального описания
        fillMenu(album)
    }

    private fun fillBody(album: Album) {
        Glide.with(binding.ivAlbumImage)
            .load(album.uri)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .into(binding.ivAlbumImage)
        binding.tvAlbumName.text = album.name
        if (album.description.isEmpty()) {
            binding.tvAlbumDescription.isVisible = false
        } else {
            binding.tvAlbumDescription.isVisible = true
        }
        binding.tvAlbumDescription.text = album.description
        binding.tvTracksTime.text = formatUtils.formatLongToTimeMillis(album.tracksMillis)
        binding.tvTracksCount.text = album.tracksCount.toString()
    }

    private fun fillMenu(album: Album) {
        Glide.with(binding.incMenuBootomSheet.incAlbum.ivAlbum)
            .load(album.uri)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .into(binding.incMenuBootomSheet.incAlbum.ivAlbum)

        binding.incMenuBootomSheet.incAlbum.tvName.text = album.name
        binding.incMenuBootomSheet.incAlbum.tvCount.text = album.tracksCount.toString()
    }

    private fun showTracksContent(tracks: List<Track>) {
        tracksAdapter.setList(tracks)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showTrackDeleteDialog(listItem: ListItemChangeState) {
        deleteDialog
            .setTitle(R.string.dialog_track_delete_title)
            .setMessage(R.string.dialog_track_delete_ask)
            .setNegativeButton(R.string.dialog_no) { _, _ ->
                // Ничего не делаем
            }
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                // Удаляем трек
                viewModel.deleteTrackFromAlbum(listItem)
            }.show()
    }

    private fun showAlbumDeleteDialog(message: String) {
        deleteDialog
            .setTitle(R.string.dialog_album_delete_title)
            .setMessage(message)
            .setNegativeButton(R.string.dialog_no) { _, _ ->
                // Ничего не делаем
            }
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                // Удаляем альбом
                viewModel.deleteAlbum()
            }
            .show()
    }

    private fun navigateToPlayer(trackId: Int, trackDataType: String) {
        findNavController().navigate(
            R.id.action_albumFragment_to_playerFragment,
            PlayerFragment.createArgs(trackId, trackDataType)
        )
    }

    private fun navigateToPlaylistEdit(albumId: Long) {
        findNavController().navigate(
            R.id.action_albumFragment_to_playlistEditFragment,
            PlaylistEditFragment.createArgs(albumId)
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}