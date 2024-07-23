package com.example.playlistmaker.data.storage

interface SetItem<T,K> {
    suspend fun set(item: T): K
}