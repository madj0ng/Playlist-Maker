package com.example.playlistmaker.data.storage.empty

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.GetItemById

class DataOfEmpty : GetItemById<Int, TrackDto> {
    override suspend fun get(id: Int): TrackDto? {
        return null
    }
}