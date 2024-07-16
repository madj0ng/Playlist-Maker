package com.example.playlistmaker.data.storage.sharedpref

import android.content.SharedPreferences
import com.example.playlistmaker.data.storage.sharedpref.dao.LocalStorage

class LocalStoreImpl(
    private val sharedPreferences: SharedPreferences,
) : LocalStorage {

    companion object {
        const val HISTORY_LIST_KEY = "history_list_key"
    }

    override suspend fun getString(): String? {
        return sharedPreferences.getString(
            HISTORY_LIST_KEY, null
        )
    }

    override suspend fun setString(jsonString: String) {
        sharedPreferences.edit()
            .putString(HISTORY_LIST_KEY, jsonString)
            .apply()
    }

    override suspend fun clear(): Boolean {
        return sharedPreferences.edit().clear().commit()
    }
}