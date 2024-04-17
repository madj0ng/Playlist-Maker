package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entity.Track

interface TrackDetailRepository {
    fun getTrackByString(trackStr: String?): Track?
}