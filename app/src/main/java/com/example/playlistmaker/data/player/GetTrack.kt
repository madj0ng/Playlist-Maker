package com.example.playlistmaker.data.player

import com.example.playlistmaker.domain.search.model.Track

interface GetTrack {
    fun execute(trackStr: String?): Track?
}