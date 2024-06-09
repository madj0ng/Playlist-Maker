package com.example.playlistmaker.domain.media.playlist.impl

import com.example.playlistmaker.domain.media.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.media.playlist.PlaylistRepository
import com.example.playlistmaker.domain.search.model.Track

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override fun loadFavourites(
        onComplete: (List<Track>) -> Unit,
        onError: (String) -> Unit
    ) {
        val list = playlistRepository.getPlayList()
        if (list.isEmpty()) {
            onError.invoke("")
        } else {
            onComplete.invoke(list)
        }
    }
}