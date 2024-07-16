package com.example.playlistmaker.data.storage.sharedpref.dao

interface LocalStorage {

    suspend fun getString(): String?

    suspend fun setString(jsonString: String)

    suspend fun clear(): Boolean
}