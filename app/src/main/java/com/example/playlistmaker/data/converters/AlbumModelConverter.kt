package com.example.playlistmaker.data.converters

import android.net.Uri
import com.example.playlistmaker.data.storage.db.entity.AlbumEntity
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.ui.playlistadd.models.AlbumState
import com.google.gson.Gson

object AlbumModelConverter {

    fun map(album: AlbumState): Album {
        return Album(
            0L,
            album.name,
            album.description,
            album.uri,
            arrayOf(),
            0
        )
    }

    fun map(album: Album): AlbumEntity {
        return AlbumEntity(
            album.id,
            album.name,
            album.description,
            album.uri?.toString() ?: "",
            Gson().toJson(album.tracksId) ?: "",
            album.tracksCount
        )
    }

    fun map(album: AlbumEntity): Album {
        return Album(
            album.id,
            album.name,
            album.description,
            Uri.parse(album.uri) ?: null,
            Gson().fromJson(album.tracksId, Array<Int>::class.java) ?: arrayOf(),
            album.tracksCount
        )
    }
}