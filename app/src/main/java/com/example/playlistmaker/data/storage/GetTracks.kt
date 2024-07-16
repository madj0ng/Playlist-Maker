package com.example.playlistmaker.data.storage

interface GetTracks<T> {
    suspend fun get(): List<T>
}