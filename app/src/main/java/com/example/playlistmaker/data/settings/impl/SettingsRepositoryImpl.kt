package com.example.playlistmaker.data.settings.impl

import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsRepositoryImpl(private val themeLocalStorage: ThemeLocalStorage) :
    SettingsRepository {
    override fun getSettingTheme(default: Boolean): ThemeSettings {
        return ThemeSettings(isDark = themeLocalStorage.read(default))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        themeLocalStorage.write(settings.isDark)
    }
}