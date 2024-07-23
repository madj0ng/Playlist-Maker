package com.example.playlistmaker.data.storage.db

import com.example.playlistmaker.data.converters.FavouriteTrackDbConvertor
import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.DeleteTrack
import com.example.playlistmaker.data.storage.GetItems
import com.example.playlistmaker.data.storage.GetTrackById
import com.example.playlistmaker.data.storage.SetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DataOfFavourites(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: FavouriteTrackDbConvertor,
) : GetItems<TrackDto>,
    SetItem<TrackDto, Unit>,
    GetTrackById<TrackDto>,
    DeleteTrack<TrackDto> {

    override suspend fun get(): List<TrackDto> {
        val tracks = withContext(Dispatchers.IO) {
            async { appDatabase.favoriteTrackDao().getFavorite() }
        }
        return tracks.await().map { track -> trackDbConvertor.map(track) }
    }

    override suspend fun get(trackId: Int): TrackDto? {
        return withContext(Dispatchers.IO) {
            val track = appDatabase.favoriteTrackDao().getFavoriteById(trackId)
            if (track != null) {
                trackDbConvertor.map(track)
            } else {
                null
            }
        }
    }

    override suspend fun set(item: TrackDto) {
        appDatabase.favoriteTrackDao().insertFavorite(trackDbConvertor.map(item))
    }

    override suspend fun delete(track: TrackDto) {
        appDatabase.favoriteTrackDao().deleteFavorite(trackDbConvertor.map(track))
    }
}