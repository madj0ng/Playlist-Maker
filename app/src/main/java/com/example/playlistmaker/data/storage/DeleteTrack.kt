package com.example.playlistmaker.data.storage

interface DeleteTrack<T> {
    suspend fun del(track: T)
}