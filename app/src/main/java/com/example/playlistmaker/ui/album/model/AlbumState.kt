package com.example.playlistmaker.ui.album.model

import com.example.playlistmaker.domain.playlistadd.model.Album

sealed interface AlbumState {
    data object Loading : AlbumState

    data class Content(val data: Album) : AlbumState

    data class Error(val errorMessage: String) : AlbumState

    data object Empty : AlbumState
}