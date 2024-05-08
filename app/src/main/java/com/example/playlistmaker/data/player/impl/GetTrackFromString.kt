package com.example.playlistmaker.data.player.impl

import com.example.playlistmaker.data.player.GetTrack
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson

class GetTrackFromString : GetTrack {
    override fun execute(trackStr: String?): Track? {
        return Gson().fromJson(trackStr, Track::class.java)
    }
}