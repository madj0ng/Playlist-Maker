package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences

class ThemeLocalStorage(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val THEME_ISDARK_KEY = "theme_isdark_key"
    }

    // чтение
    fun read(default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(THEME_ISDARK_KEY, default)
    }

    // запись
    fun write(checked: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_ISDARK_KEY, checked)
            .apply()
    }
}