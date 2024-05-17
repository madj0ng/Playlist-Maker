package com.example.playlistmaker.data.media.playlist

import com.example.playlistmaker.domain.media.playlist.PlaylistRepository
import com.example.playlistmaker.domain.search.model.Track

class PlaylistRepositoryImpl: PlaylistRepository {
    override fun getPlayList(): List<Track> {
        return mutableListOf()
    }
}