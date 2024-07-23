package com.example.playlistmaker.data.storage

interface GetItems<T> {
    suspend fun get(): List<T>
}