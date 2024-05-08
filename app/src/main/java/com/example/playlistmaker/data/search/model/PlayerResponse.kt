package com.example.playlistmaker.data.search.model

sealed interface PlayerResponse<T>{
    data class Data<T>(val value: T): PlayerResponse<T>
}