package com.example.playlistmaker.ui.playlistadd.models

sealed interface AlbumTriggerState {
    data class Album(val albumId: Long) : AlbumTriggerState
    data object Playlist : AlbumTriggerState
    data object Player : AlbumTriggerState
}