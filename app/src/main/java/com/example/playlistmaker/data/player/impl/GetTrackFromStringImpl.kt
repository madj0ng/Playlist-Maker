package com.example.playlistmaker.data.player.impl

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.domain.player.GetTrackFromString
import com.google.gson.Gson

class GetTrackFromStringImpl(private val gson: Gson) : GetTrackFromString<TrackDto> {
    override fun execute(jsonString: String): Array<TrackDto> {
        return gson.fromJson(jsonString, Array<TrackDto>::class.java)
    }
}