package com.example.playlistmaker.data.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.storage.db.dao.FavoriteTrackDao
import com.example.playlistmaker.data.storage.db.entity.FavouriteTrackEntity

@Database(version = 1, entities = [FavouriteTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

    companion object{
        @Volatile
        private lateinit var instance: AppDatabase

        fun getInstance(context: Context): AppDatabase{
            synchronized(this){
                if(!Companion::instance.isInitialized){
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java, "database.db"
                    ).allowMainThreadQueries().build()
                }
                return instance
            }
        }
    }
}