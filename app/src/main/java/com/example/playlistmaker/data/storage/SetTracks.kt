package com.example.playlistmaker.data.storage

interface SetTracks<T> {
    suspend fun set(tracks: List<T>)
}