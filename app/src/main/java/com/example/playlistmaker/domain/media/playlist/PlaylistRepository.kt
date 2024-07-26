package com.example.playlistmaker.domain.media.playlist

import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlayList(): Flow<List<Album>>
    fun addTrackToAlbum(track: Track, album: Album): Flow<Boolean>
}