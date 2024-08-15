package com.example.playlistmaker.data.storage

interface GetItemById<T, K> {
    suspend fun get(id: T): K?
}