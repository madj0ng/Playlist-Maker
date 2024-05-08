package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.player.impl.GetTrackFromString
import com.example.playlistmaker.data.search.HistoryRepository
import com.example.playlistmaker.data.search.LocalStorage
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource

class HistoryRepositoryImpl(private val searchHistory: LocalStorage) : HistoryRepository {
    private val trackList = ArrayList<Track>()

    init {
        trackList.clear()
        trackList.addAll(toTracksFromString(searchHistory.getHistory()))
    }

    override fun getHistory(): Resource<ArrayList<Track>> {
        return Resource.Success(this.trackList)
    }

    override fun setHistory(track: Track) {
        checkHistoryTrack(track)
    }

    override fun saveHistory() = searchHistory.saveHistory(toStringFromTracks(this.trackList))

    override fun clearHistory() {
        trackList.clear()
    }

    private fun toStringFromTracks(trackList: ArrayList<Track>): Set<String> {
        val list = trackList.map { SetTrackToString().execute(it) }.toMutableList()
        return list.asReversed().toMutableSet()
    }

    private fun toTracksFromString(trackList: Set<String>): ArrayList<Track> {
        val mutableList = ArrayList<Track>()
        for (trackString in trackList) {
            val track = GetTrackFromString().execute(trackString)
            if (track != null) {
                mutableList.add(track)
            }
//            when(response){
//                is Resource.Error -> {}
//                is Resource.Success -> { mutableList.add(response.data)}
//            }
        }
        return mutableList
    }

    private fun checkHistoryTrack(track: Track) {
        val maxSize = 9
        val startPosition = 0
        var i = startPosition + 1

        this.trackList.add(startPosition, track)
        while (i < this.trackList.size) {
            // Удаляем трек, если он уже есть в истории и его индекс больше 9
            if (i > maxSize || this.trackList[i].trackId == track.trackId) {
                this.trackList.removeAt(i)
                i--
            }
            i++
        }
    }
}