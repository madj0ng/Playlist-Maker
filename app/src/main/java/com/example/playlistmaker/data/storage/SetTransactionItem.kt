package com.example.playlistmaker.data.storage

interface SetTransactionItem<T, K> {
    suspend fun setTransactionItem(item: T): K
}