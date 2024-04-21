package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.NetworkResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesSearchService = retrofit.create(ItunesSearchApi::class.java)

    override fun doRequest(dto: Any): NetworkResponse {
        return if (dto is TracksSearchRequest) {
            val resp = itunesSearchService.getTracks(dto.expression).execute()
            val body = resp.body() ?: NetworkResponse()

            body.apply { resultCode = resp.code() }
        } else {
            NetworkResponse().apply { resultCode = 400 }
        }
    }
}