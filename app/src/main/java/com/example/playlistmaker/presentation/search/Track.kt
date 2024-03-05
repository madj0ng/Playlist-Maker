package com.example.playlistmaker.presentation.search

data class Track(
    val trackId: Int,                   // Название композиции
    val trackName: String,              // Название композиции
    val artistName: String,             // Имя исполнителя
    val trackTimeMillis: Long,          // Продолжительность трека в миллисекундах
    val artworkUrl100: String,          // Ссылка на изображение обложки
    val collectionName: String = "",    // Название альбома
    val releaseDate: String,            // Год релиза трека
    val primaryGenreName: String,       // Жанр трека
    val country: String,                // Страна исполнителя
    val previewUrl: String              // Ссылка на отрывок трека
){
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}