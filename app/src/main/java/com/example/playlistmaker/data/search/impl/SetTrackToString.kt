package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.domain.search.SetTrack
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson

class SetTrackToString(private val gson: Gson) : SetTrack {
    override fun execute(track: Track): String {
        return gson.toJson(track)
    }
}