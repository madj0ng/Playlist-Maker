package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.mapper.TrackMapper
import com.example.playlistmaker.data.search.model.TracksSearchRequest
import com.example.playlistmaker.data.search.model.TracksSearchResponse
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : SearchRepository {
    override fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(message = response.errMessage))
            }

            200 -> {
                val tracks =
                    (response as TracksSearchResponse).results?.map { trackMapper.map(it) } as ArrayList<Track>
                emit(Resource.Success(tracks))
            }

            else -> {
                emit(Resource.Success(arrayListOf()))
            }
        }
    }
}