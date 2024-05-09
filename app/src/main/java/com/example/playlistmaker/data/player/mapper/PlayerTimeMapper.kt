package com.example.playlistmaker.data.player.mapper

import com.example.playlistmaker.data.search.model.PlayerResponse
import com.example.playlistmaker.util.Resource

object PlayerTimeMapper {
    fun map(responseDto: PlayerResponse<Int>): Resource<Int> {
        return when (responseDto) {
            is PlayerResponse.Data -> {
                Resource.Success(responseDto.value)
            }
        }
    }
}