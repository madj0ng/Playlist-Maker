package com.example.playlistmaker.data.storage.sharedpref

import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.DeleteTracks
import com.example.playlistmaker.data.storage.GetItemById
import com.example.playlistmaker.data.storage.GetItems
import com.example.playlistmaker.data.storage.SetItem
import com.example.playlistmaker.data.storage.sharedpref.dao.LocalStorage
import com.example.playlistmaker.domain.player.GetTrackFromString
import com.example.playlistmaker.domain.search.SetTrackToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DataOfHistory(
    private val localStore: LocalStorage,
    private val setTrackToString: SetTrackToString<TrackDto>,
    private val getTrackFromString: GetTrackFromString<TrackDto>,
) : GetItems<TrackDto>,
    SetItem<TrackDto, Unit>,
    GetItemById<Int, TrackDto>,
    DeleteTracks {

    override suspend fun get(): List<TrackDto> {
        val stringList = withContext(Dispatchers.IO) {
            async { localStore.getString() }
        }
        return withContext(Dispatchers.Default) {
            when (val tracks =
                stringList.await()?.let { getTrackFromString.execute(it).toList() }) {
                null -> listOf()
                else -> tracks
            }
        }
    }

    override suspend fun get(id: Int): TrackDto? {
        val trackList = this.get()
        return trackList.find { it.trackId == id }
    }

    override suspend fun set(item: TrackDto) {
        val trackList = checkTrackBeforAdd(item)
        val tracksSet = withContext(Dispatchers.Default) {
            async { setTrackToString.execute(trackList) }
        }
        withContext(Dispatchers.IO) {
            localStore.setString(tracksSet.await())
        }
    }

    override suspend fun delete() {
        withContext(Dispatchers.IO) {
            localStore.clear()
        }
    }

    private suspend fun checkTrackBeforAdd(track: TrackDto): MutableList<TrackDto> {
        val maxSize = 9
        val startPosition = 0
        var i = startPosition + 1
        val trackList = this.get().toMutableList()
        trackList.add(startPosition, track)
        while (i < trackList.size) {
            // Удаляем трек, если он уже есть в истории и его индекс больше 9
            if (i > maxSize || trackList[i].trackId == track.trackId) {
                trackList.removeAt(i)
                i--
            }
            i++
        }
        return trackList
    }
}