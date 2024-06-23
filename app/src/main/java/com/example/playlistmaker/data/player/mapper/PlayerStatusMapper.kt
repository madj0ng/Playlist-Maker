package com.example.playlistmaker.data.player.mapper

import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.data.search.model.PlayerStatusDto
import com.example.playlistmaker.domain.player.model.PlayerStatus
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
            PlayerStatusDto.DEFAULT -> PlayerStatus.Default()
            PlayerStatusDto.PAUSED -> PlayerStatus.Paused(0)
            PlayerStatusDto.PLAYING -> PlayerStatus.Playing(0)
            PlayerStatusDto.PREPARED -> PlayerStatus.Prepared()
        }
    }
}