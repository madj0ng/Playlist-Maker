package com.example.playlistmaker.domain.media.playlist

import com.example.playlistmaker.domain.search.model.Track

interface PlaylistRepository {
    fun getPlayList(): List<Track>
}