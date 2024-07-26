package com.example.playlistmaker.data.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playlistmaker.creator.DB_FAVOURITES
import com.example.playlistmaker.data.storage.db.dao.AlbumDao
import com.example.playlistmaker.data.storage.db.dao.FavoriteTrackDao
import com.example.playlistmaker.data.storage.db.dao.TrackDao
import com.example.playlistmaker.data.storage.db.entity.AlbumEntity
import com.example.playlistmaker.data.storage.db.entity.FavouriteTrackEntity
import com.example.playlistmaker.data.storage.db.entity.TrackEntity

@Database(
    version = 1, entities = [
        FavouriteTrackEntity::class,
        AlbumEntity::class,
        TrackEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

    abstract fun albumDao(): AlbumDao

    abstract fun trackDao(): TrackDao

    companion object {
        @Volatile
        private lateinit var instance: AppDatabase

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                if (!Companion::instance.isInitialized) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java, DB_FAVOURITES
                    ).allowMainThreadQueries().build()
                }
                return instance
            }
        }
    }
}