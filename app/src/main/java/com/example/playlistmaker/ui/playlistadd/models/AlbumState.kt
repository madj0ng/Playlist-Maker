package com.example.playlistmaker.ui.playlistadd.models

import android.net.Uri

data class AlbumState (
    val name: String,
    val description: String,
    val uri: Uri?
)