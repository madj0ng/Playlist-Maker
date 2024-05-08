package com.example.playlistmaker.data.search.storage

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.LocalStorage

class HistoryLocalStorage(private val sharedPreferences: SharedPreferences) : LocalStorage {
    companion object {
        const val HISTORY_LIST_KEY = "history_list_key"
    }

    override fun addHistory(track: String, remove: Boolean) {
        val mutableSet = getHistory().toMutableSet()
        val modified = if (remove) mutableSet.remove(track) else mutableSet.add(track)
        if (modified) sharedPreferences.edit().putStringSet(HISTORY_LIST_KEY, mutableSet).apply()
    }

    override fun getHistory(): Set<String> {
        return sharedPreferences.getStringSet(HISTORY_LIST_KEY, emptySet()) ?: emptySet()
    }

    override fun saveHistory(trackList: Set<String>) {
        sharedPreferences.edit().putStringSet(HISTORY_LIST_KEY, trackList).apply()
    }
}
//
//fun getSaved(key: String): Set<String> {
//    return sharedPreferences.getStringSet(key, emptySet()) ?: emptySet()
//}
//
//fun save(key: String, trackList: Set<String>){
//    sharedPreferences.edit().putStringSet(key, trackList).apply()
//}
//
//fun change(key: String, track: String, remove: Boolean) {
//    val mutableSet = getSaved(key = key).toMutableSet()
//    val modified = if (remove) mutableSet.remove(track) else mutableSet.add(track)
//    if (modified) sharedPreferences.edit().putStringSet(key, mutableSet).apply()
//}