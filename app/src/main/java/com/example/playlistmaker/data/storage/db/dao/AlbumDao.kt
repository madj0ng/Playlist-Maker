package com.example.playlistmaker.data.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.data.storage.db.entity.AlbumEntity
import com.example.playlistmaker.data.storage.db.entity.TrackEntity

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(albumEntity: AlbumEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM album")
    suspend fun readAlbums(): List<AlbumEntity>

    @Transaction
    suspend fun insertTrackToAlbum(trackEntity: TrackEntity, albumEntity: AlbumEntity){
        insertAlbum(albumEntity)
        insertTrack(trackEntity)
    }
}