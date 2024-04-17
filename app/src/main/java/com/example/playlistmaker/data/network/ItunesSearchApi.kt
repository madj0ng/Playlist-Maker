package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchApi {

    @GET("search?entity=song")
    fun getTracks(@Query("term") term: String): Call<TracksSearchResponse>
}