package com.example.playlistmaker.data.player.impl

import com.example.playlistmaker.data.converters.TrackSharedConvertor
import com.example.playlistmaker.data.player.PlayerClient
import com.example.playlistmaker.data.player.mapper.PlayerStatusMapper
import com.example.playlistmaker.data.player.mapper.PlayerTimeMapper
import com.example.playlistmaker.data.search.model.PlayerRequest
import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.data.search.model.TrackDto
import com.example.playlistmaker.data.storage.DeleteItem
import com.example.playlistmaker.data.storage.GetItemById
import com.example.playlistmaker.data.storage.SetItem
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.player.model.PlayerStatus
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlayerRepositoryImpl(
    private val playerClient: PlayerClient,
    private val playerTimeMapper: PlayerTimeMapper,
    private val playerStatusMapper: PlayerStatusMapper,
    private val getTrackById: GetItemById<Int, TrackDto?>,
    private val getFavouriteTrackById: GetItemById<Int, TrackDto?>,
    private val deleteFavouriteTracks: DeleteItem<TrackDto, Unit>,
    private val setFavouriteTrack: SetItem<TrackDto, Unit>,
    private val trackMapper: TrackSharedConvertor,
) : PlayerRepository {

    override fun getTrackById(trackId: Int): Flow<Resource<Track>> = flow {
        val trackDto = getTrackById.get(trackId)
        if (trackDto != null) {
            emit(Resource.Success(setFavourite(trackMapper.map(trackDto))))
        } else {
            emit(Resource.Error(""))
        }
    }

    override fun onFavouritePressed(track: Track): Flow<Boolean> = flow {
        if (track.isFavourite) {
            deleteFavouriteTracks.delete(trackMapper.map(track))
        } else {
            setFavouriteTrack.set(trackMapper.map(track))
        }
        track.isFavourite = !track.isFavourite
        emit(track.isFavourite)
    }

    override fun preparePlayerSuspend(track: Track): Flow<Resource<PlayerStatus>> = flow {
        when (val playerResponse =
            playerClient.preparePlayerSuspend(PlayerRequest(track.previewUrl))) {
            is PlayerResponse.Data -> emit(playerStatusMapper.map(playerResponse))
        }
    }

    override fun getPlayerStatus(): Flow<Resource<Boolean>> = flow {
        when (val playerResponse = playerClient.getPlayerStatus()) {
            is PlayerResponse.Data -> emit(Resource.Success(playerResponse.value))
        }
    }

    override fun startPlayer() {
        playerClient.start()
    }

    override fun pausePlayer() {
        playerClient.pause()
    }

    override fun setCompletionListenerSuspend(): Flow<Resource<PlayerStatus>> = flow {
        when (val playerResponse = playerClient.setOnCompletionListenerSuspend()) {
            is PlayerResponse.Data -> emit(playerStatusMapper.map(playerResponse))
        }
    }

    override fun getTimePlayer(): Resource<Int> {
        val playerResponse = playerClient.getCurrentPosition()
        return playerTimeMapper.map(playerResponse)
    }

    override fun clearPlayer() {
        playerClient.release()
    }

    private suspend fun setFavourite(track: Track): Track {
        if (getFavouriteTrackById != getTrackById) {
            track.isFavourite = (getFavouriteTrackById.get(track.trackId) != null)
        }
        return track
    }
}