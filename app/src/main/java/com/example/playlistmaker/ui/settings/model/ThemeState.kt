package com.example.playlistmaker.ui.settings.model

sealed interface ThemeState {
    data class Dark(val isVisible: Boolean = true): ThemeState
    data class Light(val isVisible: Boolean = false): ThemeState
}