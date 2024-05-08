package com.example.playlistmaker.ui.search.models

sealed interface SearchState {

    data object Loading : SearchState

    data object Search: SearchState

    data object History : SearchState

    data class Failure(val errorMessage: String) : SearchState

    data object Error : SearchState
}