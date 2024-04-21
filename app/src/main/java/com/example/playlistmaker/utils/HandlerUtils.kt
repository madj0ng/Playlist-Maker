package com.example.playlistmaker.utils

import android.os.Handler

object HandlerUtils {
    private const val CLICK_DEBOUNCE_DELAY = 1000L
    const val SEARCH_DEBOUNCE_DELAY = 2000L
    const val TIME_DEBOUNCE_DELAY = 300L

    private var isClickAllowed = true

    fun clickDebounce(handler: Handler): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }
}