package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.converters.TrackSharedConvertor
import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.DeleteTracks
import com.example.playlistmaker.data.storage.GetItems
import com.example.playlistmaker.data.storage.SetItem
import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val trackMapper: TrackSharedConvertor,
    private val getTracks: GetItems<TrackDto>,
    private val setTrack: SetItem<TrackDto,Unit>,
    private val deleteTracks: DeleteTracks,
) : HistoryRepository {

    override fun getHistory(): Flow<Resource<ArrayList<Track>>> = flow {
        val tracks = getTracks.get().map { trackMapper.map(it) } as ArrayList<Track>
        emit(Resource.Success(tracks))
    }

    override suspend fun setHistory(track: Track) {
        setTrack.set(trackMapper.map(track))
    }

    override suspend fun clearHistory() {
        deleteTracks.delete()
    }
}