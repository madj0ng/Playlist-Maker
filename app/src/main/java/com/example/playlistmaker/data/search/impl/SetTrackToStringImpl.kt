package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.domain.search.SetTrackToString
import com.google.gson.Gson

class SetTrackToStringImpl(private val gson: Gson) : SetTrackToString<TrackDto> {
    override fun execute(tracks: List<TrackDto>): String {
        return gson.toJson(tracks)
    }
}