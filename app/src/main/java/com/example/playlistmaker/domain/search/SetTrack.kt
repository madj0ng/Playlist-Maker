package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

fun interface SetTrack {
    fun execute(track: Track): String
}