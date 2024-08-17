package com.example.playlistmaker.data.playlistadd.impl

import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.storage.db.DataOfAlbum
import com.example.playlistmaker.data.storage.file.DataOfFile
import com.example.playlistmaker.domain.playlistadd.PlaylistAddRepository
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistAddRepositoryImpl(
    private val dataOfAlbum: DataOfAlbum,
    private val dataOfFile: DataOfFile
) : PlaylistAddRepository {

    override fun getAlbum(albumId: Long): Flow<Resource<Album>> = flow {
        val album = dataOfAlbum.getAlbumById(albumId)
        emit(
            if(album != null){
                Resource.Success(album)
            }else{
                Resource.Error("")
            }
        )
    }

    override fun addAlbum(album: Album): Flow<Int> = flow {
        dataOfAlbum.set(album)
        emit(R.string.playlistadd_create_success)
    }

    override fun updateAlbum(album: Album): Flow<Int> = flow {
        dataOfAlbum.set(album)
        emit(R.string.playlistadd_update_success)
    }

    override fun saveImageToPrivateStorage(uri: Uri?): Flow<Uri?> = flow {
        var privateUri: Uri? = null
        if (uri != null) {
            privateUri = dataOfFile.set(uri)
        }
        emit(privateUri)
    }

    override fun deleteImageFromPrivateStorage(uri: Uri?) = flow {
        if(uri != null) {
            dataOfFile.delete(uri)
        }
        emit(Unit)
    }
}