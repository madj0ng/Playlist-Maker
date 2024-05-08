package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun getSettingsTheme(default: Boolean): ThemeSettings {
        return settingsRepository.getSettingTheme(default)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSetting(settings)
    }
}