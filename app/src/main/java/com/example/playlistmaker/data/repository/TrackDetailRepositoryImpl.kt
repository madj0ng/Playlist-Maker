package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.TrackDetailRepository
import com.google.gson.Gson

class TrackDetailRepositoryImpl: TrackDetailRepository {
    override fun getTrackByString(trackStr: String?): Track? {
        return Gson().fromJson(trackStr, Track::class.java)
    }
}