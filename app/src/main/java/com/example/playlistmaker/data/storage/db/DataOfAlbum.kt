package com.example.playlistmaker.data.storage.db

import com.example.playlistmaker.data.converters.AlbumModelConverter
import com.example.playlistmaker.data.storage.GetItems
import com.example.playlistmaker.data.storage.SetItem
import com.example.playlistmaker.domain.playlistadd.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataOfAlbum(
    private val appDatabase: AppDatabase,
    private val albumModelConverter: AlbumModelConverter,
) : SetItem<Album, Unit>, GetItems<Album> {

    override suspend fun set(item: Album) {
        withContext(Dispatchers.Default) {
            appDatabase.albumDao().insertAlbum(albumModelConverter.map(item))
        }
    }

    override suspend fun get(): List<Album> {
        return withContext(Dispatchers.Default) {
            appDatabase.albumDao().readAlbums().map { album -> albumModelConverter.map(album) }
        }
    }
}