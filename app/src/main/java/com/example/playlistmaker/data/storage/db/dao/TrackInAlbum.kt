package com.example.playlistmaker.data.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.data.storage.db.entity.TrackEntity
import com.example.playlistmaker.data.storage.db.entity.TrackInAlbumEntity

@Dao
interface TrackInAlbum : AlbumDao, TrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInAlbum(trackInAlbumEntity: TrackInAlbumEntity): Long

    @Delete(entity = TrackInAlbumEntity::class)
    suspend fun deleteTrackInAlbum(trackInAlbum: TrackInAlbumEntity): Int

    @Query("SELECT * FROM track_in_album")
    suspend fun getTrackInAlbumAll(): List<TrackInAlbumEntity>

    @Query("SELECT * FROM track_in_album WHERE albumId = :albumId AND trackId = :trackId")
    suspend fun getTrackInAlbum(albumId: Long, trackId: Int): TrackInAlbumEntity?

    @Query("SELECT * FROM track_in_album WHERE albumId = :albumId")
    suspend fun getTrackInAlbumByAlbumId(albumId: Long): List<TrackInAlbumEntity>

    @Query("SELECT * FROM track_in_album WHERE trackId = :trackId")
    suspend fun getTrackInAlbumByTrackId(trackId: Int): List<TrackInAlbumEntity>

    @Transaction
    suspend fun insertTrackToAlbum(albumId: Long, track: TrackEntity): Int {
        val album = readAlbumById(albumId)
        return if (album != null) {
            if (getTrackInAlbum(albumId, track.trackId) == null) {
                // Добавляем трек
                insertTrack(track)
                // Добавляем в таблицу мэппинга
                insertTrackInAlbum(TrackInAlbumEntity(albumId, track.trackId))
                // Обновляем плейлист
                val resAlbum = updateAlbum(
                    album.copy(
                        tracksCount = album.tracksCount + 1,
                        tracksMillis = album.tracksMillis + track.trackTimeMillis
                    )
                )
                1
            } else {
                0
            }
        } else {
            0
        }
    }

    @Transaction
    suspend fun deleteTrackFromAlbum(albumId: Long, trackId: Int): Int {
        val track = getTrackById(trackId)
        val album = readAlbumById(albumId)
        return if (album != null && track != null) {
            // Удаляем запись в таблице мэппинга
            deleteTrackInAlbum(TrackInAlbumEntity(albumId, trackId))
            // Удаляем трек из таблицы треков, если он отсутствует в плейлистах
            if (getTrackInAlbumByTrackId(trackId).isEmpty()) {
                deleteTrackById(trackId)
            }
            // Обновляем данные плейлиста
            updateAlbum(
                album.copy(
                    tracksCount = album.tracksCount - 1,
                    tracksMillis = album.tracksMillis - track.trackTimeMillis
                )
            )
            1
        } else {
            0
        }
    }

    @Transaction
    suspend fun deleteAlbum(albumId: Long): Int {
        val trackInAlbumAll = getTrackInAlbumAll()
        // мэппинг для плейлиста albumId
        val trackInAlbumByAlbumId = trackInAlbumAll
            .filter {
                it.albumId == albumId
            }
            .map {
                // Удаляем мэппинг для плейлиста albumId
                deleteTrackInAlbum(TrackInAlbumEntity(it.albumId, it.trackId))
                it
            }
        // треки не встречающиеся в других плейлистах
        val trackInAlbumForDelete = trackInAlbumByAlbumId
            .filter { trackInAlbum ->
                trackInAlbumAll.find {
                    it.albumId != trackInAlbum.albumId
                    it.trackId == trackInAlbum.trackId
                } == null
            }
            .map {
                // Удаляем треки не встречающиеся в других плейлистах
                deleteTrackById(it.trackId)
            }
        return deleteAlbumById(albumId)
    }
}