package com.example.playlistmaker.domain.album

import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface AlbumInteractor {
    fun getTracksOfAlbum(albumId: Long): Flow<List<Track>>
    fun getAlbumData(albumId: Long): Flow<Pair<Album?, String?>>
    fun deleteTrackFromAlbum(albumId: Long, trackId: Int): Flow<Boolean>
    fun deleteAlbum(albumId: Long): Flow<Boolean>
    fun shareApp(albumId: Long): Flow<Pair<Boolean?, String?>>
}