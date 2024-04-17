package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.PlayerResponse
import com.example.playlistmaker.domain.entity.Resource

object PlayerTimeMapper {
    fun map(responseDto: PlayerResponse<Int>): Resource<Int> {
        return when (responseDto) {
            is PlayerResponse.Data -> {
                Resource.Success(responseDto.value)
            }
        }
    }
}