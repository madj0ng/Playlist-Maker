package com.example.playlistmaker.ui.playlistadd.models

interface AlbumStateUi {
    data object Loading : AlbumStateUi
    data class Content(val data: AlbumUi) : AlbumStateUi
}