package com.example.playlistmaker.data.storage.db

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.DeleteTrack
import com.example.playlistmaker.data.storage.GetTrackById
import com.example.playlistmaker.data.storage.GetTracks
import com.example.playlistmaker.data.storage.SetTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DataOfFavourites(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : GetTracks<TrackDto>,
    SetTrack<TrackDto>,
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

    override suspend fun set(track: TrackDto) {
        appDatabase.favoriteTrackDao().insertFavorite(trackDbConvertor.map(track))
    }

    override suspend fun del(track: TrackDto) {
        appDatabase.favoriteTrackDao().deleteFavorite(trackDbConvertor.map(track))
    }
}