package com.example.playlistmaker.ui.album.model

import com.example.playlistmaker.domain.search.model.Track

data class ListItemChangeState(
    val track: Track,
    val position: Int = 0,
    val rangeStart: Int = 0,
    val rangeEnd: Int = 0
)
