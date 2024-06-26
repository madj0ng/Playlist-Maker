package com.example.playlistmaker.data.player.impl

import com.example.playlistmaker.domain.player.GetTrack
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson

class GetTrackFromString(private val gson: Gson) : GetTrack {
    override fun execute(trackStr: String?): Track? {
        return gson.fromJson(trackStr, Track::class.java)
    }
}