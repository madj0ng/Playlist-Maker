package com.example.playlistmaker.util.consumer

interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}