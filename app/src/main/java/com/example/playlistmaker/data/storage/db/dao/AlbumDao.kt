package com.example.playlistmaker.data.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.storage.db.entity.AlbumEntity

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(albumEntity: AlbumEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAlbum(albumEntity: AlbumEntity): Int

    @Query("DELETE FROM album WHERE id = :albumId")
    suspend fun deleteAlbumById(albumId: Long): Int

    @Query("SELECT * FROM album")
    suspend fun readAlbums(): List<AlbumEntity>

    @Query("SELECT * FROM album WHERE id = :albumId")
    suspend fun readAlbumById(albumId: Long): AlbumEntity?
}