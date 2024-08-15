package com.example.playlistmaker.data.storage

interface UpdateItem<T,K> {
    suspend fun update(item: T): K
}