package com.example.playlistmaker.domain.playlistadd.impl

import android.net.Uri
import com.example.playlistmaker.domain.playlistadd.PlaylistAddInteractor
import com.example.playlistmaker.domain.playlistadd.PlaylistAddRepository
import com.example.playlistmaker.domain.playlistadd.model.Album
import kotlinx.coroutines.flow.Flow

class PlaylistAddInteractorImpl(
    private val playlistAddRepository: PlaylistAddRepository
) : PlaylistAddInteractor {
    override fun albumCreate(album: Album): Flow<Int> {
        return playlistAddRepository.addAlbum(album)
    }

    override fun saveImageToPrivateStorage(uri: Uri?): Flow<Uri?> {
        return playlistAddRepository.saveImageToPrivateStorage(uri)
    }
}