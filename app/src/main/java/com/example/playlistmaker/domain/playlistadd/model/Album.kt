package com.example.playlistmaker.domain.playlistadd.model

import android.net.Uri

data class Album(
    val id: Long,
    val name: String,
    val description: String,
    val uri: Uri?,
    val tracksId: Array<Int>,
    val tracksCount: Int
)
