package com.example.playlistmaker.data.dto

sealed interface PlayerResponse<T>{
    data class Data<T>(val value: T): PlayerResponse<T>
}