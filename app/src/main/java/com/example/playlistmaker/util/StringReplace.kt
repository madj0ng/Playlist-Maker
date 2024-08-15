package com.example.playlistmaker.util

import android.app.Application
import android.content.res.Resources

fun stringReplace(application: Application, resId: Int, newString: String): String? {
    return try {
        application.getString(resId).replace("%1", newString)
    } catch (_: Resources.NotFoundException) {
        null
    }
}