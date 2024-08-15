package com.example.playlistmaker.data.storage.db.entity

import androidx.room.Entity

@Entity(tableName = "track_in_album", primaryKeys = ["albumId", "trackId"])
data class TrackInAlbumEntity(
    val albumId: Long,
    val trackId: Int
)
