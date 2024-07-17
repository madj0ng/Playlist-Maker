package com.example.playlistmaker.data.storage.empty

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.GetTrackById

class DataOfEmpty : GetTrackById<TrackDto> {
    override suspend fun get(trackId: Int): TrackDto? {
        return null
    }
}