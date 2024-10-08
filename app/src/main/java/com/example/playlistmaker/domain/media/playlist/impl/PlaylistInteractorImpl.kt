package com.example.playlistmaker.domain.media.playlist.impl

import com.example.playlistmaker.domain.media.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.media.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
) : PlaylistInteractor {

    override fun loadFavourites(): Flow<Pair<List<Album>?, String?>> {
        return playlistRepository.getPlayList().map { albums ->
            if (albums.isNotEmpty()) {
                Pair(albums, null)
            } else {
                Pair(null, null)
            }
        }
    }

    override fun addTrackToAlbum(albumId: Long, track: Track): Flow<Boolean> {
        return playlistRepository.addTrackToAlbum(albumId, track)
    }
}