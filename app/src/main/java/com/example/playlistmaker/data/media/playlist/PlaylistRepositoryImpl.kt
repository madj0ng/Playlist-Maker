package com.example.playlistmaker.data.media.playlist

import com.example.playlistmaker.data.media.playlist.model.InsertTrackToAlbumRequest
import com.example.playlistmaker.data.storage.db.DataOfAlbum
import com.example.playlistmaker.domain.media.playlist.PlaylistRepository
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val dataOfAlbum: DataOfAlbum,
) : PlaylistRepository {

    override fun getPlayList(): Flow<List<Album>> = flow {
        emit(dataOfAlbum.get())
    }

    override fun addTrackToAlbum(track: Track, album: Album): Flow<Boolean> = flow {
        dataOfAlbum.set(
            InsertTrackToAlbumRequest(
                track, album.copy(
                    tracksId = (album.tracksId + track.trackId),
                    tracksCount = album.tracksCount + 1
                )
            )
        )
        emit(true)
//        dataOfTrack.set(trackSharedConvertor.map(track))
//        dataOfAlbum.set(
//            album.copy(
//                tracksId = (album.tracksId + track.trackId),
//                tracksCount = album.tracksCount + 1
//            )
//        )
//        emit(R.string.playlistadd_add_track)
    }
}