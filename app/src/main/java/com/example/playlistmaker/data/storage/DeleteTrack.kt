package com.example.playlistmaker.data.storage

interface DeleteTrack<T> {
    suspend fun delete(track: T)
}