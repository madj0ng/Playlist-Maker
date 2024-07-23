package com.example.playlistmaker.util

import android.app.Application
import android.content.res.Resources

fun stringReplace(application: Application, ResId: Int, newString: String) : String?{
    try {
        return application.getString(ResId).replace("%1", newString)
    } catch (_: Resources.NotFoundException) {
        return null
    }
}