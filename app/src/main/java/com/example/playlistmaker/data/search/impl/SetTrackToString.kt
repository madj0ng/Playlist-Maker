package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.SetTrack
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson

class SetTrackToString : SetTrack {
    override fun execute(track: Track): String {
        return Gson().toJson(track)
    }
}