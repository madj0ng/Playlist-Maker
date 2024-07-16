package com.example.playlistmaker.creator

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.settings.SettingsInteractor
import dataModule
import interactorModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import repositoryModule
import utilModule
import viewModelModule

const val APP_PREFERENCES = "app_preferences"
const val SEARCH_PREFERENCES = "search_preferences"

const val TYPE_SEARCH = "TYPE_SEARCH"
const val TYPE_HISTORY = "TYPE_HISTORY"
const val TYPE_FAVOURITES = "TYPE_FAVOURITES"

class App : Application() {
    private var isDarkTheme = false

    private val settingsInteractor: SettingsInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Добавление контекста в граф
            androidContext(this@App)
            // Передаём все модули
            modules(utilModule, dataModule, interactorModule, repositoryModule, viewModelModule)
        }

        isDarkTheme = settingsInteractor.getSettingsTheme(
            resources.configuration.uiMode == AppCompatDelegate.MODE_NIGHT_YES
        ).isDark

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