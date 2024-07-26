package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.db.entity.FavouriteTrackEntity

class FavouriteTrackDbConvertor {

    fun map(track: TrackDto): FavouriteTrackEntity {
        return FavouriteTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate ?: "",
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            FavouriteTrackEntity.setReservationDate()
        )
    }

    fun map(track: FavouriteTrackEntity): TrackDto {
        return TrackDto(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            true
        )
    }
}