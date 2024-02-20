package com.example.playlistmaker.presentation.settings

import android.content.SharedPreferences

class SettingTheme(private val sharedPref: SharedPreferences) {
    companion object{
        const val THEME_ISDARK_KEY = "theme_isdark_key"
    }

    // чтение
    fun read(default: Boolean = false): Boolean {
        return sharedPref.getBoolean(THEME_ISDARK_KEY, default)
    }

    // запись
    fun write(checked: Boolean) {
        sharedPref.edit()
            .putBoolean(THEME_ISDARK_KEY, checked)
            .apply()
    }
}