package com.example.playlistmaker.data.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.storage.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackEntity: TrackEntity): Long

    @Query("DELETE FROM all_tracks WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Int): Int

    @Query("SELECT * FROM all_tracks")
    suspend fun readTracks(): List<TrackEntity>

    @Query("SELECT * FROM all_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): TrackEntity?
}