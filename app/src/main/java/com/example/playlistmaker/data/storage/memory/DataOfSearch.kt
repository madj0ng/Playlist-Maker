package com.example.playlistmaker.data.storage.memory

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.DeleteTracks
import com.example.playlistmaker.data.storage.GetTrackById
import com.example.playlistmaker.data.storage.GetTracks
import com.example.playlistmaker.data.storage.SetTracks

class DataOfSearch :
    GetTracks<TrackDto>,
    SetTracks<TrackDto>,
    GetTrackById<TrackDto>,
    DeleteTracks {
    private val trackList: MutableList<TrackDto> = mutableListOf()

    override suspend fun get(): MutableList<TrackDto> {
        return this.trackList
    }

    override suspend fun get(trackId: Int): TrackDto? {
        return this.trackList.find { it.trackId == trackId }
    }

    override suspend fun set(tracks: List<TrackDto>) {
        this.del()
        this.trackList.addAll(tracks)
    }

    override suspend fun del() {
        trackList.clear()
    }
}