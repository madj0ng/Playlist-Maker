package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.Track

interface GetTrack {
    fun execute(trackStr: String?): Track?
}