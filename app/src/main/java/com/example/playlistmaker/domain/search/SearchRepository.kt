package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>>
}