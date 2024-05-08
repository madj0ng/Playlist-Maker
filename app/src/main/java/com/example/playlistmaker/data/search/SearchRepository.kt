package com.example.playlistmaker.data.search

import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.domain.search.model.Track

interface SearchRepository {
    fun searchTracks(expression: String): Resource<ArrayList<Track>>
}