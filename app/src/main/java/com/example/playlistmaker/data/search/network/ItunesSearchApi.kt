package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.model.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchApi {

    @GET("search?entity=song")
    suspend fun getTracks(@Query("term") term: String): TracksSearchResponse
}