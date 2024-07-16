package com.example.playlistmaker.data.storage

interface SetTrack<T> {
    suspend fun set(track: T)
}