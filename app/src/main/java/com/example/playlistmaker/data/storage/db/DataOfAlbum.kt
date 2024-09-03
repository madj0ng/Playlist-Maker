package com.example.playlistmaker.data.storage.db

import com.example.playlistmaker.data.album.model.TrackInAlbumRequest
import com.example.playlistmaker.data.converters.AlbumModelConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.media.playlist.model.InsertTrackInAlbumRequest
import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.DeleteItem
import com.example.playlistmaker.data.storage.DeleteTransactionItem
import com.example.playlistmaker.data.storage.GetItemById
import com.example.playlistmaker.data.storage.GetItems
import com.example.playlistmaker.data.storage.GetTransactionItems
import com.example.playlistmaker.data.storage.SetItem
import com.example.playlistmaker.data.storage.SetTransactionItem
import com.example.playlistmaker.data.storage.UpdateItem
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DataOfAlbum(
    private val appDatabase: AppDatabase,
    private val albumModelConverter: AlbumModelConverter,
    private val trackDbConverter: TrackDbConverter,
) : SetItem<Album, Unit>,
    SetTransactionItem<InsertTrackInAlbumRequest, Boolean>,
    GetItems<Album>,
    GetTransactionItems<Long, Track>,
    GetItemById<Int, TrackDto?>,
    DeleteItem<Long, Boolean>,
    DeleteTransactionItem<TrackInAlbumRequest, Boolean>,
    UpdateItem<Album, Int> {

    override suspend fun set(item: Album) {
        withContext(Dispatchers.Default) {
            appDatabase.albumDao().insertAlbum(albumModelConverter.map(item))
        }
    }

    override suspend fun setTransactionItem(item: InsertTrackInAlbumRequest): Boolean {
        return withContext(Dispatchers.Default) {
            when (appDatabase.trackInAlbum().insertTrackToAlbum(
                item.albumId,
                trackDbConverter.map(item.track)
            )) {
                0 -> false
                else -> true
            }
        }
    }

    override suspend fun get(): List<Album> {
        return withContext(Dispatchers.Default) {
            appDatabase.albumDao().readAlbums().map { album -> albumModelConverter.map(album) }
        }
    }

    override suspend fun getTransactionItems(data: Long): List<Track> {
        val trackInAlbum = withContext(Dispatchers.IO) {
            async { appDatabase.trackInAlbum().getTrackInAlbumByAlbumId(data) }
        }
        return appDatabase.trackDao().readTracks()
            .filter { track ->
                trackInAlbum.await().find { it.trackId == track.trackId } != null
            }.map { trackDbConverter.map(it) }
    }

    suspend fun getAlbumById(id: Long): Album? {
        return withContext(Dispatchers.IO) {
            val album = appDatabase.albumDao().readAlbumById(id)
            if (album != null) {
                albumModelConverter.map(album)
            } else {
                null
            }
        }
    }

    override suspend fun get(id: Int): TrackDto? {
        return withContext(Dispatchers.IO) {
            val track = appDatabase.trackDao().getTrackById(id)
            if (track != null) {
                trackDbConverter.mapToDto(track)
            } else {
                null
            }
        }
    }

    override suspend fun deleteTransactionItem(item: TrackInAlbumRequest): Boolean {
        return withContext(Dispatchers.IO) {
            when (appDatabase.trackInAlbum().deleteTrackFromAlbum(item.albumId, item.trackId)) {
                0 -> false
                else -> true
            }
        }
    }

    override suspend fun delete(item: Long): Boolean {
        return withContext(Dispatchers.IO) {
            when (appDatabase.trackInAlbum().deleteAlbum(item)) {
                0 -> false
                else -> true
            }
        }
    }

    override suspend fun update(item: Album): Int {
        return withContext(Dispatchers.IO) {
            appDatabase.albumDao().updateAlbum(albumModelConverter.map(item))
        }
    }
}