package com.example.playlistmaker.util

sealed interface Resource<T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error<T>(val message: String) : Resource<T>
}