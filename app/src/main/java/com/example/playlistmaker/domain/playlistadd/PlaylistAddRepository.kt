package com.example.playlistmaker.domain.playlistadd

import android.net.Uri
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface PlaylistAddRepository {
    fun getAlbum(albumId: Long): Flow<Resource<Album>>
    fun addAlbum(album: Album): Flow<Int>
    fun updateAlbum(album: Album): Flow<Int>
    fun saveImageToPrivateStorage(uri: Uri?): Flow<Uri?>
    fun deleteImageFromPrivateStorage(uri: Uri?): Flow<Unit>
}