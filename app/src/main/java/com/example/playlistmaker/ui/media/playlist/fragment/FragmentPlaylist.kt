package com.example.playlistmaker.ui.media.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.playlist.models.PlaylistState
import com.example.playlistmaker.ui.media.playlist.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentPlaylist : Fragment() {
    companion object {
        fun newInstance(): FragmentPlaylist {
            return FragmentPlaylist()
        }
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by activityViewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeScreenState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.data)
            is PlaylistState.Empty -> showEmpty(state.message)
            is PlaylistState.Error -> showError(state.errorMessage)
            is PlaylistState.Loading -> showLoading()
        }
    }

    private fun showContent(trackList: List<Track>) {
        binding.btEmpty.visibility = View.GONE
        binding.ivEmpty.visibility = View.GONE
        binding.tvEpmty.visibility = View.GONE
        binding.playList.visibility = View.VISIBLE
    }

    private fun showEmpty(message: String) {
        binding.btEmpty.visibility = View.VISIBLE
        binding.ivEmpty.visibility = View.VISIBLE
        binding.tvEpmty.visibility = View.VISIBLE
        binding.playList.visibility = View.GONE
    }

    private fun showError(message: String) {}
    private fun showLoading() {}
}