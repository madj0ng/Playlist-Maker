package com.example.playlistmaker.data.storage

interface GetTrackById<T> {
    suspend fun get(trackId: Int): T?
}