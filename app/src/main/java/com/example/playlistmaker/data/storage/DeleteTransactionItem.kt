package com.example.playlistmaker.data.storage

interface DeleteTransactionItem<T, K> {
    suspend fun deleteTransactionItem(item: T): K
}