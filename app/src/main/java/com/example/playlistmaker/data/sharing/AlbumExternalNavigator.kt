package com.example.playlistmaker.data.sharing

import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track

interface AlbumExternalNavigator {
    fun shareLink(album: Album, tracks: List<Track>): Boolean
}