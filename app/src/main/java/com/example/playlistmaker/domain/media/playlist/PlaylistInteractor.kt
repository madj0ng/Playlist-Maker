package com.example.playlistmaker.domain.media.playlist

import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun loadFavourites(): Flow<Pair<List<Album>?, String?>>
    fun addTrackToAlbum(albumId: Long, track: Track): Flow<Boolean>
}