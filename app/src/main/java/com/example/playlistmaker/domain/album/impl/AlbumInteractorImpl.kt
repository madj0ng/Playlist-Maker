package com.example.playlistmaker.domain.album.impl

import com.example.playlistmaker.domain.album.AlbumInteractor
import com.example.playlistmaker.domain.album.AlbumRepository
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlbumInteractorImpl(
    private val albumRepository: AlbumRepository
) : AlbumInteractor {
    override fun getTracksOfAlbum(albumId: Long): Flow<List<Track>> {
        return albumRepository.getTracksOfAlbum(albumId)
    }

    override fun getAlbumData(albumId: Long): Flow<Pair<Album?, String?>> {
        return albumRepository.getAlbum(albumId).map { result ->
            when (result) {
                is Resource.Error -> Pair(null, result.message)
                is Resource.Success -> Pair(result.data, null)
            }
        }
    }

    override fun deleteTrackFromAlbum(albumId: Long, trackId: Int): Flow<Boolean> {
        return albumRepository.deleteTrackFromAlbum(albumId, trackId)
    }

    override fun deleteAlbum(albumId: Long): Flow<Boolean> {
        return albumRepository.deleteAlbum(albumId)
    }

    override fun shareApp(albumId: Long): Flow<Pair<Boolean?, String?>> {
        return albumRepository.shareApp(albumId).map { result ->
            when (result) {
                is Resource.Error -> Pair(null, result.message)
                is Resource.Success -> Pair(result.data, null)
            }
        }
    }
}