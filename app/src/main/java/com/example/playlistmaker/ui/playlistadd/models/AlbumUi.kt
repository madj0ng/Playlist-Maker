package com.example.playlistmaker.ui.playlistadd.models

import android.net.Uri

data class AlbumUi (
    val name: String,
    val description: String,
    val uri: Uri?
)