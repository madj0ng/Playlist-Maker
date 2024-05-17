package com.example.playlistmaker.domain.media.playlist

import com.example.playlistmaker.domain.search.model.Track

interface PlaylistInteractor {
    fun loadFavourites(
        onComplete: (List<Track>) -> Unit,
        onError: (String) -> Unit
    )
}