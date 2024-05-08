package com.example.playlistmaker.data.settings

import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsRepository {
    fun getSettingTheme(default: Boolean = false): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}