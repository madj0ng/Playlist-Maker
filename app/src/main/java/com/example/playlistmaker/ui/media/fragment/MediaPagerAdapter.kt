package com.example.playlistmaker.ui.media.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.ui.media.favourites.fragment.FragmentFavourites
import com.example.playlistmaker.ui.media.playlist.fragment.FragmentPlaylist

class MediaPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    companion object {
        const val PAGE_COUNT = 2
    }

    override fun getItemCount(): Int = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            FragmentFavourites.newInstance()
        } else {
            FragmentPlaylist.newInstance()
        }
    }
}