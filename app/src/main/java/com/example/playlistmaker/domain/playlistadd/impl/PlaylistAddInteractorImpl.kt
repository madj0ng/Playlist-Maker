package com.example.playlistmaker.domain.playlistadd.impl

import android.net.Uri
import com.example.playlistmaker.domain.playlistadd.PlaylistAddInteractor
import com.example.playlistmaker.domain.playlistadd.PlaylistAddRepository
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistAddInteractorImpl(
    private val playlistAddRepository: PlaylistAddRepository
) : PlaylistAddInteractor {
    override fun getAlbum(albumId: Long): Flow<Pair<Album?, String?>> {
        return playlistAddRepository.getAlbum(albumId).map {
            when (it) {
                is Resource.Success -> Pair(it.data, null)
                is Resource.Error -> Pair(null, it.message)
            }
        }
    }

    override fun albumCreate(album: Album): Flow<Int> {
        return playlistAddRepository.addAlbum(album)
    }

    override fun albumUpdate(album: Album): Flow<Int> {
        return playlistAddRepository.updateAlbum(album)
    }

    override fun saveImageToPrivateStorage(uri: Uri?): Flow<Uri?> {
        return playlistAddRepository.saveImageToPrivateStorage(uri)
    }

    override fun deleteImageFromPrivateStorage(uri: Uri?) {
        playlistAddRepository.deleteImageFromPrivateStorage(uri)
    }
}