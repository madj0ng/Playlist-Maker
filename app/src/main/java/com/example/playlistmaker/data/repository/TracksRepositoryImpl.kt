package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.entity.Resource
import com.example.playlistmaker.domain.entity.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Resource<ArrayList<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return if (response is TracksSearchResponse) {
            if (response.resultCode == 200) {
                val tracks = response.results.map {TrackMapper.map(it) } as ArrayList<Track>
                Resource.Success(tracks)
            } else {
                Resource.Success(arrayListOf())
            }
        }else{
            Resource.Error("Произошла сетевая ошибка")
        }
//        return if (response.resultCode == 200) {
//            (response as TracksSearchResponse).results.map {
//                TrackMapper.map(it)
//            } as ArrayList<Track>
//        } else {
//            arrayListOf()
//        }
    }
}