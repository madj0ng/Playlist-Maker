package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.creator.App
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.ui.settings.model.ThemeState

class SettingsViewModel(
    private val application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : AndroidViewModel(application) {

    private val switcherLiveData = MutableLiveData<ThemeState>(
        getThemeFromBoolean(
            settingsInteractor.getSettingsTheme(
                application.resources.configuration.uiMode == AppCompatDelegate.MODE_NIGHT_YES
            ).isDark
        )
    )

    fun observeTheme(): LiveData<ThemeState> = switcherLiveData

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun updateSwitcher(darkThemeEnabled: Boolean) {
        (application as App).switchTheme(darkThemeEnabled)
        settingsInteractor.updateThemeSetting(ThemeSettings(darkThemeEnabled))

        setSwitcher(
            getThemeFromBoolean(darkThemeEnabled)
        )
    }

    private fun getThemeFromBoolean(darkThemeEnabled: Boolean): ThemeState {
        return when (darkThemeEnabled) {
            true -> ThemeState.Dark
            false -> ThemeState.Light
        }
    }

    private fun setSwitcher(theme: ThemeState) {
        switcherLiveData.postValue(theme)
    }
}