package com.example.playlistmaker.data.storage.memory

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.DeleteTracks
import com.example.playlistmaker.data.storage.GetItemById
import com.example.playlistmaker.data.storage.GetItems
import com.example.playlistmaker.data.storage.SetTracks

class DataOfSearch :
    GetItems<TrackDto>,
    SetTracks<TrackDto>,
    GetItemById<Int, TrackDto>,
    DeleteTracks {
    private val trackList: MutableList<TrackDto> = mutableListOf()

    override suspend fun get(): MutableList<TrackDto> {
        return this.trackList
    }

    override suspend fun get(id: Int): TrackDto? {
        return this.trackList.find { it.trackId == id }
    }

    override suspend fun set(tracks: List<TrackDto>) {
        this.delete()
        this.trackList.addAll(tracks)
    }

    override suspend fun delete() {
        trackList.clear()
    }
}