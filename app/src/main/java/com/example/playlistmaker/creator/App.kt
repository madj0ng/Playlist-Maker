package com.example.playlistmaker.creator

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.SettingsInteractor

const val APP_PREFERENCES = "app_preferences"
const val SEARCH_PREFERENCES = "search_preferences"

class App : Application() {
    private var isDarkTheme = false

    lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        settingsInteractor =
            Creator.providerSettingsInteractor((this as Application).applicationContext)
        isDarkTheme = settingsInteractor.getSettingsTheme(
            resources.configuration.uiMode == AppCompatDelegate.MODE_NIGHT_YES
        ).isDark

//        isDarkTheme = ThemeLocalStorage(
//            getSharedPreferences(
//                APP_PREFERENCES,
//                MODE_PRIVATE
//            )
//        ).read(resources.configuration.uiMode == AppCompatDelegate.MODE_NIGHT_YES)
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}