package com.example.playlistmaker.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object DebounceUtils {
    const val CLICK_DEBOUNCE_DELAY = 1000L
    const val SEARCH_DEBOUNCE_DELAY = 2000L
    const val TIME_DEBOUNCE_DELAY = 300L
    const val TIME_DEBOUNCE_EMPTY = 0L

    private var isClickAllowed = true

    fun clickDebounce(coroutineScope: CoroutineScope): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            coroutineScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}