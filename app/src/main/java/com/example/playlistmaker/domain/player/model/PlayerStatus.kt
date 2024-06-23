package com.example.playlistmaker.domain.player.model

import com.example.playlistmaker.ui.player.activity.PlayerActivity

sealed class PlayerStatus(val progress: Long) {
    class Default : PlayerStatus(PlayerActivity.TRACK_START_TIME)
    class Prepared : PlayerStatus(PlayerActivity.TRACK_START_TIME)
    class Playing(progress: Long) : PlayerStatus(progress)
    class Paused(progress: Long) : PlayerStatus(progress)
}