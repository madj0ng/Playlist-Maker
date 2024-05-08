package com.example.playlistmaker.domain.settings

import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsInteractor {
    fun getSettingsTheme(default: Boolean = false): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}