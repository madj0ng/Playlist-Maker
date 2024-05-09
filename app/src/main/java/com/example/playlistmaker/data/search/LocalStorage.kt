package com.example.playlistmaker.data.search

interface LocalStorage {
    fun addHistory(track: String, remove: Boolean = false)

    fun getHistory(): Set<String>

    fun saveHistory(trackList: Set<String>)
}