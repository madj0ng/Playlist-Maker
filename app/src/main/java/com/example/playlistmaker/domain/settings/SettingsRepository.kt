package com.example.playlistmaker.domain.settings

import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsRepository {
    fun getSettingTheme(default: Boolean = false): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}