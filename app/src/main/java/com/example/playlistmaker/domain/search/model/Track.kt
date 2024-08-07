package com.example.playlistmaker.domain.search.model

data class Track(
    val trackId: Int,               // ID композиции
    val trackName: String,          // Название композиции
    val artistName: String,         // Имя исполнителя
    val trackTimeMillis: Long,      // Продолжительность трека в формате mm:ss
    val artworkUrl100: String,      // Ссылка на изображение обложки
    val collectionName: String,     // Название альбома
    val releaseDate: String,        // Год релиза трека
    val primaryGenreName: String,   // Жанр трека
    val country: String,            // Страна исполнителя
    val previewUrl: String,         // Ссылка на отрывок трека
    var isFavourite: Boolean        // Трек в избранном
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}