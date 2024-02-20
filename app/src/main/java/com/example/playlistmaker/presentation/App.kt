package com.example.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.presentation.settings.SettingTheme

const val APP_PREFERENCES = "app_preferences"
const val SEARCH_PREFERENCES = "search_preferences"

class App : Application() {
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        darkTheme = SettingTheme(
            getSharedPreferences(
                APP_PREFERENCES,
                MODE_PRIVATE
            )
        ).read(resources.configuration.uiMode == AppCompatDelegate.MODE_NIGHT_YES)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}