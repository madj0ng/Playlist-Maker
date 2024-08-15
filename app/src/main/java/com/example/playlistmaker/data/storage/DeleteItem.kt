package com.example.playlistmaker.data.storage

interface DeleteItem<T, K> {
    suspend fun delete(item: T): K
}