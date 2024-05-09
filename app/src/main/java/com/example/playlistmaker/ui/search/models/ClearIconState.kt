package com.example.playlistmaker.ui.search.models

sealed interface ClearIconState {
    data class None(val isVisible: Boolean = false): ClearIconState
    data class Show(val isVisible: Boolean = true): ClearIconState
}