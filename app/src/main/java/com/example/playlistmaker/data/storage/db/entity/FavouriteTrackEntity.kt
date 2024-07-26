package com.example.playlistmaker.data.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(tableName = "favorite_tracks")
data class FavouriteTrackEntity(
    @PrimaryKey
    val trackId: Int,               // Уникальный номер композиции
    val trackName: String,          // Название композиции
    val artistName: String,         // Имя исполнителя
    val trackTimeMillis: Long,      // Продолжительность трека в миллисекундах
    val artworkUrl100: String?,     // Ссылка на изображение обложки
    val collectionName: String?,    // Название альбома
    val releaseDate: String,        // Год релиза трека
    val primaryGenreName: String,   // Жанр трека
    val country: String,            // Страна исполнителя
    val previewUrl: String?,        // Ссылка на отрывок трека
    val reservationDate: Long
) {
    companion object {
        fun setReservationDate(): Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    }
}
