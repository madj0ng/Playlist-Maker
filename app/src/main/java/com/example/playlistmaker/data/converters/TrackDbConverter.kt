package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.db.entity.TrackEntity
import com.example.playlistmaker.domain.search.model.Track
import java.time.LocalDateTime
import java.time.ZoneOffset

object TrackDbConverter {
    fun map(track: TrackDto): TrackEntity {
        return TrackEntity(
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
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100.ifEmpty { null },
            if (track.collectionName.isNotEmpty()) track.collectionName else null,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            if (track.previewUrl.isNotEmpty()) track.previewUrl else null,
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )
    }

}