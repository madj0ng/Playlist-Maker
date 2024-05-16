package com.example.playlistmaker.ui.media.favourites.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.favourites.models.FavouritesState
import com.example.playlistmaker.ui.media.favourites.view_model.FavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentFavourites : Fragment() {
    companion object {
        fun newInstance(): FragmentFavourites {
            return FragmentFavourites()
        }
    }

    private lateinit var binding: FragmentFavouritesBinding

    private val viewModel: FavouritesViewModel by activityViewModel<FavouritesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeScreenState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavouritesState) {
        when (state) {
            is FavouritesState.Content -> showContent(state.data)
            is FavouritesState.Empty -> showEmpty(state.message)
            is FavouritesState.Error -> showError(state.errorMessage)
            is FavouritesState.Loading -> showLoading()
        }
    }

    private fun showContent(trackList: List<Track>) {
        binding.ivEmpty.visibility = View.GONE
        binding.tvEpmty.visibility = View.GONE
        binding.favourites.visibility = View.VISIBLE
    }

    private fun showEmpty(message: String) {
        binding.ivEmpty.visibility = View.VISIBLE
        binding.tvEpmty.visibility = View.VISIBLE
        binding.favourites.visibility = View.GONE
    }

    private fun showError(message: String) {}
    private fun showLoading() {}

}