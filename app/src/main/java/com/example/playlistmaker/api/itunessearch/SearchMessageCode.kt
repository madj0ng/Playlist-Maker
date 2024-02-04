package com.example.playlistmaker.api.itunessearch

import android.view.View

// Настройки отображения сообщения-заглушки в зависимости от возвращаемого ответа
enum class SearchMessageCode(
    val code: Int?,
    val viewMessage: Int,
    val viewText: Int,
    val viewButtom: Int,
    val message: String = "",
    val image: String = ""
) {
    SUCCESS(200, View.GONE, View.GONE, View.GONE),
    ERROR(0, View.VISIBLE, View.VISIBLE, View.GONE, "error_search_find", "error_find"),
    FAILURE(null, View.VISIBLE, View.VISIBLE, View.VISIBLE, "error_connect_find", "error_connect");
}

