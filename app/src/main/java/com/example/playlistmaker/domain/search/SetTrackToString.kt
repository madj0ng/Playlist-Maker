package com.example.playlistmaker.domain.search

interface SetTrackToString<T> {
    fun execute(tracks: List<T>): String
}