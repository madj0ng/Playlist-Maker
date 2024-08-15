package com.example.playlistmaker.ui.playlistedit.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.playlistadd.fragment.PlaylistAddFragment
import com.example.playlistmaker.ui.playlistadd.models.PlaylistUi
import com.example.playlistmaker.ui.playlistedit.view_model.PlaylistEditViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlaylistEditFragment : PlaylistAddFragment() {

    companion object {
        const val ALBUM_KEY = "album_key"

        fun createArgs(albumId: Long): Bundle =
            bundleOf(
                ALBUM_KEY to albumId,
            )
    }

    override lateinit var viewModel: PlaylistEditViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val albumId = requireArguments().getLong(ALBUM_KEY)
        viewModel = getViewModel { parametersOf(albumId) }

        super.onViewCreated(view, savedInstanceState)

        render(
            PlaylistUi(
                requireContext().getString(R.string.playlistedit_title),
                requireContext().getString(R.string.playlistedit_save)
            )
        )
    }
}