package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.SearchRepository
import com.example.playlistmaker.data.search.mapper.TrackMapper
import com.example.playlistmaker.data.search.model.TracksSearchRequest
import com.example.playlistmaker.data.search.model.TracksSearchResponse
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : SearchRepository {
    override fun searchTracks(expression: String): Resource<ArrayList<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return when (response.resultCode) {
            -1 -> {
                Resource.Error(message = response.errMessage)
            }

            200 -> {
                val tracks =
                    (response as TracksSearchResponse).results?.map { trackMapper.map(it) } as ArrayList<Track>
                Resource.Success(tracks)
            }

            else -> {
                Resource.Success(arrayListOf())
            }
        }
    }
}