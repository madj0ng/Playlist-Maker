package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.search.model.NetworkResponse

interface NetworkClient {
    fun doRequest(dto: Any): NetworkResponse
}