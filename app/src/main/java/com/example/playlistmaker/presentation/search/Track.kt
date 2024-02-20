package com.example.playlistmaker.presentation.search

data class Track(
    val trackId: Int,      // Название композиции
    val trackName: String,      // Название композиции
    val artistName: String,     // Имя исполнителя
    var trackTime: String,      // Продолжительность трека в формате mm:ss
    val trackTimeMillis: Long,  // Продолжительность трека в миллисекундах
    val artworkUrl100: String   // Ссылка на изображение обложки
)