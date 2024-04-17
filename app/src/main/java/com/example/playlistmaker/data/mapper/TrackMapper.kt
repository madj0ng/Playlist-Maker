package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.entity.Track

object TrackMapper {
    fun map(track: TrackDto): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100 ?: "",
            track.collectionName ?: "",
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl ?: ""
        )
    }
}