package com.example.playlistmaker.ui.settings.model

sealed interface ThemeState {
    data object Dark: ThemeState
    data object Light: ThemeState
}