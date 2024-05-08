package com.example.playlistmaker.data.player.mapper

import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.data.search.model.PlayerStatusDto
import com.example.playlistmaker.domain.search.model.PlayerStatus
import com.example.playlistmaker.util.Resource

object PlayerStatusMapper {
    fun map(responseDto: PlayerResponse<PlayerStatusDto>): Resource<PlayerStatus> {
        return when (responseDto) {
            is PlayerResponse.Data -> {
                Resource.Success(mapStatus(responseDto.value))
            }
        }
    }

    private fun mapStatus(status: PlayerStatusDto): PlayerStatus {
        return when (status) {
            PlayerStatusDto.DEFAULT -> PlayerStatus.DEFAULT
            PlayerStatusDto.PAUSED -> PlayerStatus.PAUSED
            PlayerStatusDto.PLAYING -> PlayerStatus.PLAYING
            PlayerStatusDto.PREPARED -> PlayerStatus.PREPARED
        }
    }
}