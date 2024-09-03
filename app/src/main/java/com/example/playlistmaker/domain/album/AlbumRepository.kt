package com.example.playlistmaker.domain.album

import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getTracksOfAlbum(albumId: Long): Flow<List<Track>>
    fun getAlbum(albumId: Long): Flow<Resource<Album>>
    fun deleteTrackFromAlbum(albumId: Long, trackId: Int): Flow<Boolean>
    fun deleteAlbum(albumId: Long): Flow<Boolean>
    fun shareApp(albumId: Long): Flow<Resource<Boolean>>
}