package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.NetworkResponse

interface NetworkClient {
    fun doRequest(dto: Any): NetworkResponse
}