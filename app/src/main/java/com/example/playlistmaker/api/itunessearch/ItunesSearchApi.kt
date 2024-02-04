package com.example.playlistmaker.api.itunessearch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchApi {

    @GET("search?entity=song")
    fun getTracks(@Query("term") text: String): Call<ItunesSearchResponse>
}