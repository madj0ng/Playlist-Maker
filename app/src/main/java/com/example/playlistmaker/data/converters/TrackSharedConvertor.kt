package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.domain.search.model.Track

class TrackSharedConvertor {
    fun map(track: TrackDto): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100 ?: "",
            track.collectionName ?: "",
            track.releaseDate ?: "",
            track.primaryGenreName,
            track.country,
            track.previewUrl ?: "",
            track.isFavourite
        )
    }

    fun map(track: Track): TrackDto {
        return TrackDto(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100.ifEmpty { null },
            if (track.collectionName.isEmpty()) null else track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            if (track.previewUrl.isEmpty()) null else track.previewUrl,
            track.isFavourite
        )
    }
}