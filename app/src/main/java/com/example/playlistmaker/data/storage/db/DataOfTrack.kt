package com.example.playlistmaker.data.storage.db

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.SetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataOfTrack(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConverter,
) : SetItem<TrackDto, Long> {

    override suspend fun set(item: TrackDto): Long {
        return withContext(Dispatchers.IO) {
            appDatabase.trackDao().insertTrack(trackDbConvertor.map(item))
        }
    }
}