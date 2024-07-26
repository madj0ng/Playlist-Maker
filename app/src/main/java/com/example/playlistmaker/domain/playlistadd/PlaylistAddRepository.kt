package com.example.playlistmaker.domain.playlistadd

import android.net.Uri
import com.example.playlistmaker.domain.playlistadd.model.Album
import kotlinx.coroutines.flow.Flow

interface PlaylistAddRepository {
    fun addAlbum(album: Album): Flow<Int>
    fun saveImageToPrivateStorage(uri: Uri?): Flow<Uri?>
}