package com.example.playlistmaker.util

sealed interface RequestResult {
    data class Success<T>(val value: T) : RequestResult
    data class Error<K>(val value: K) : RequestResult
}