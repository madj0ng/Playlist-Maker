package com.example.playlistmaker.ui.playlistadd.models

import com.example.playlistmaker.creator.TYPE_FAVOURITES
import com.example.playlistmaker.creator.TYPE_PLAYER

sealed interface AlbumTriggerState {
    data class Playlist(val type: String = TYPE_FAVOURITES): AlbumTriggerState
    data class Player(val type: String = TYPE_PLAYER): AlbumTriggerState
}