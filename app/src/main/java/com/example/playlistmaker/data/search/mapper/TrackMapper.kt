package com.example.playlistmaker.data.search.mapper

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.domain.search.model.Track

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