package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.search.model.NetworkResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetworkResponse
}