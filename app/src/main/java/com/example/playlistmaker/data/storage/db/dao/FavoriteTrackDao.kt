package com.example.playlistmaker.data.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.storage.db.entity.FavouriteTrackEntity

@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(tracks: List<FavouriteTrackEntity>)

    @Query("SELECT * FROM favorite_tracks ORDER BY reservationDate DESC")
    suspend fun getFavorite(): List<FavouriteTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(track: FavouriteTrackEntity)

    @Delete(entity = FavouriteTrackEntity::class)
    suspend fun deleteFavorite(track: FavouriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks WHERE trackId = :trackId")
    suspend fun getFavoriteById(trackId: Int): FavouriteTrackEntity?
}