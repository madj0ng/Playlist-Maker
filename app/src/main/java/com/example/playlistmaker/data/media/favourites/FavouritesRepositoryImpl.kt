package com.example.playlistmaker.data.media.favourites

import com.example.playlistmaker.data.converters.TrackSharedConvertor
import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.GetItems
import com.example.playlistmaker.domain.media.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val trackSharedConvertor: TrackSharedConvertor,
    private val getTracks: GetItems<TrackDto>,
) : FavouritesRepository {

    override fun favoritesTracks(): Flow<List<Track>> = flow {
        val tracks = getTracks.get()
        emit(convertFromTrackDto(tracks))
    }

    private fun convertFromTrackDto(tracks: List<TrackDto>): List<Track> {
        return tracks.map { track -> trackSharedConvertor.map(track) }
    }
}