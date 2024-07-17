package com.example.playlistmaker.domain.player

interface GetTrackFromString<T> {
    fun execute(jsonString: String): Array<T>
}