package com.example.playlistmaker.data.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album")
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String,
    val uri: String?,
    val tracksCount: Int,
    val tracksMillis: Long,
)