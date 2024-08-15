package com.example.playlistmaker.data.media.playlist

import com.example.playlistmaker.data.media.playlist.model.InsertTrackInAlbumRequest
import com.example.playlistmaker.data.storage.db.DataOfAlbum
import com.example.playlistmaker.domain.media.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val dataOfAlbum: DataOfAlbum,
) : PlaylistRepository {

    override fun getPlayList(): Flow<List<Album>> = flow {
        emit(dataOfAlbum.get())
    }

    override fun addTrackToAlbum(albumId: Long, track: Track): Flow<Boolean> = flow {
        emit(dataOfAlbum.setTransactionItem(InsertTrackInAlbumRequest(albumId, track)))
    }
}