package com.example.playlistmaker.data.album

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.data.album.model.TrackInAlbumRequest
import com.example.playlistmaker.data.sharing.AlbumExternalNavigator
import com.example.playlistmaker.data.storage.db.DataOfAlbum
import com.example.playlistmaker.domain.album.AlbumRepository
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlbumRepositoryImpl(
    private val context: Context,
    private val dataOfAlbum: DataOfAlbum,
    private val albumExternalNavigator: AlbumExternalNavigator,
) : AlbumRepository {

    override fun getTracksOfAlbum(albumId: Long): Flow<List<Track>> = flow {
        emit(dataOfAlbum.getTransactionItems(albumId))
    }

    override fun getAlbum(albumId: Long): Flow<Resource<Album>> = flow {
        val album = dataOfAlbum.getAlbumById(albumId)
        if (album != null) {
            emit(Resource.Success(album))
        } else {
            emit(Resource.Error(context.getString(R.string.error_album_find)))
        }
    }

    override fun deleteTrackFromAlbum(albumId: Long, trackId: Int): Flow<Boolean> = flow {
        emit(dataOfAlbum.deleteTransactionItem(TrackInAlbumRequest(albumId, trackId)))
    }

    override fun deleteAlbum(albumId: Long): Flow<Boolean> = flow {
        emit(dataOfAlbum.delete(albumId))
    }

    override fun shareApp(albumId: Long): Flow<Resource<Boolean>> = flow {
        val album = dataOfAlbum.getAlbumById(albumId)
        emit(
            if (album != null) {
                val tracks = dataOfAlbum.getTransactionItems(albumId)
                if (tracks.isEmpty()) {
                    Resource.Error(context.getString(R.string.album_share_empty))
                } else {
                    when (albumExternalNavigator.shareLink(album, tracks)) {
                        true -> {
                            Resource.Success(true)
                        }

                        false -> {
                            Resource.Error(context.getString(R.string.error_find))
                        }
                    }
                }
            } else {
                Resource.Error(context.getString(R.string.error_album_find))
            }
        )
    }
}