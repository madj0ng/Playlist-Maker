package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.PlayerResponse
import com.example.playlistmaker.data.dto.PlayerStatusDto
import com.example.playlistmaker.domain.entity.PlayerStatus
import com.example.playlistmaker.domain.entity.Resource

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