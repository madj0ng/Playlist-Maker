package com.example.playlistmaker.domain.playlistadd

import android.net.Uri
import com.example.playlistmaker.domain.playlistadd.model.Album
import kotlinx.coroutines.flow.Flow

interface PlaylistAddInteractor {
    fun albumCreate(album: Album): Flow<Int>
    fun saveImageToPrivateStorage(uri: Uri?): Flow<Uri?>
}