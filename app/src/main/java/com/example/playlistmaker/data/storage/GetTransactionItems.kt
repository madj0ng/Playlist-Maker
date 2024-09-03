package com.example.playlistmaker.data.storage

interface GetTransactionItems<T, K> {
    suspend fun getTransactionItems(data: T): List<K>
}