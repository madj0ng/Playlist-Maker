package com.example.playlistmaker.ui.media.favourites.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.TYPE_FAVOURITES
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.favourites.models.FavouritesState
import com.example.playlistmaker.ui.media.favourites.view_model.FavouritesViewModel
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.search.models.TrackTriggerState
import com.example.playlistmaker.util.DebounceUtils
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavouritesFragment : Fragment() {
    companion object {
        fun newInstance(): FavouritesFragment {
            return FavouritesFragment()
        }
    }

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouritesViewModel by activityViewModel<FavouritesViewModel>()

    private lateinit var favouriteAdapter: FavouritesAdapter
    private lateinit var onFavouriteTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onFavouriteTrackClickDebounce = debounce(
            DebounceUtils.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { track ->
            viewModel.startActiviryPlayer(track, TYPE_FAVOURITES)
        }
        favouriteAdapter = FavouritesAdapter { track -> onFavouriteTrackClickDebounce(track) }

        // Список
        binding.favourites.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewModel.observeScreenState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowTrackTrigger().observe(viewLifecycleOwner) {
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

    private fun render(state: TrackTriggerState) {
        when (state) {
            is TrackTriggerState.Favourites -> navigateToDetails(state.trackId, state.dataType)
            else -> {}
        }
    }

    private fun render(state: FavouritesState) {
        when (state) {
            is FavouritesState.Content -> showContent(state.data)
            is FavouritesState.Empty -> showEmpty()
            is FavouritesState.Error -> showError(state.errorMessage)
            is FavouritesState.Loading -> showLoading()
        }
    }

    private fun showContent(trackList: List<Track>) {
        with(binding) {
            progressBar.visibility = View.GONE
            favourites.visibility = View.VISIBLE
            errorGroup.visibility = View.GONE
            favourites.adapter = favouriteAdapter
            favouriteAdapter.setList(trackList)
            favouriteAdapter.notifyDataSetChanged()
        }
    }

    private fun showEmpty() {
        with(binding) {
            progressBar.visibility = View.GONE
            favourites.visibility = View.GONE
            errorGroup.visibility = View.VISIBLE
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            favourites.visibility = View.GONE
            errorGroup.visibility = View.GONE
        }
    }

    private fun navigateToDetails(trackId: Int, trackDataType: String) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playerFragment,
            PlayerFragment.createArgs(trackId, trackDataType)
        )
    }

    private fun showError(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}