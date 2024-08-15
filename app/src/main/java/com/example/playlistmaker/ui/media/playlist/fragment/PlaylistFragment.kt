package com.example.playlistmaker.ui.media.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.ui.album.fragment.AlbumFragment
import com.example.playlistmaker.ui.media.playlist.models.PlaylistState
import com.example.playlistmaker.ui.media.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.playlistadd.models.AlbumTriggerState
import com.example.playlistmaker.util.DebounceUtils
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaylistFragment : Fragment() {
    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by activityViewModel<PlaylistViewModel>()

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var onPlaylistTrackClickDebounce: (Album) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylistTrackClickDebounce = debounce(
            DebounceUtils.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) {
            render(AlbumTriggerState.Album(it.id))
        }
        playlistAdapter = PlaylistAdapter { album -> onPlaylistTrackClickDebounce(album) }

        // Список
        binding.playList.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.btPlaylistAdd.setOnClickListener {
            render(AlbumTriggerState.Playlist)
        }

        viewModel.observeScreenState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.data)
            is PlaylistState.Empty -> showEmpty()
            is PlaylistState.Error -> showError(state.errorMessage)
            is PlaylistState.Loading -> showLoading()
        }
    }

    private fun showContent(list: List<Album>) {
        with(binding) {
            playlistAdapter.setList(list)
            playList.adapter = playlistAdapter
            btPlaylistAdd.visibility = View.VISIBLE
            playList.visibility = View.VISIBLE
            grEmpty.visibility = View.GONE
            pbLoading.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        with(binding) {
            btPlaylistAdd.visibility = View.VISIBLE
            playList.visibility = View.GONE
            grEmpty.visibility = View.VISIBLE
            pbLoading.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        with(binding) {
            btPlaylistAdd.visibility = View.VISIBLE
            playList.visibility = View.GONE
            grEmpty.visibility = View.GONE
            pbLoading.visibility = View.VISIBLE
        }
    }

    private fun render(state: AlbumTriggerState) {
        when (state) {
            is AlbumTriggerState.Playlist -> navigateToAdd()
            is AlbumTriggerState.Album -> navigateToAlbum(state.albumId)
            is AlbumTriggerState.Player -> {}
        }
    }

    private fun navigateToAdd() {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playlistAddFragment
        )
    }

    private fun navigateToAlbum(albumId: Long) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_albumFragment,
            AlbumFragment.createArgs(albumId)
        )
    }
}