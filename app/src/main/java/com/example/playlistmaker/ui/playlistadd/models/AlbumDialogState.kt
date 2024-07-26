package com.example.playlistmaker.ui.playlistadd.models

interface AlbumDialogState {
    data class Show(val isEnabled: Boolean): AlbumDialogState
    data class None(val isEnabled: Boolean): AlbumDialogState
}