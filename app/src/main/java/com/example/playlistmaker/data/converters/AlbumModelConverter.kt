package com.example.playlistmaker.data.converters

import android.net.Uri
import com.example.playlistmaker.data.storage.db.entity.AlbumEntity
import com.example.playlistmaker.domain.playlistadd.model.Album

object AlbumModelConverter {

    fun map(album: Album): AlbumEntity {
        return AlbumEntity(
            album.id,
            album.name,
            album.description,
            album.uri?.toString(),
            album.tracksCount,
            album.tracksMillis
        )
    }

    fun map(album: AlbumEntity): Album {
        return Album(
            album.id,
            album.name,
            album.description,
            if(album.uri != null) Uri.parse(album.uri) else null,
            album.tracksCount,
            album.tracksMillis
        )
    }
}