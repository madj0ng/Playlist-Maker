package com.example.playlistmaker.utils

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

class FormatUtils {
    companion object {
        fun formatLongToTrakTime(lTime: Long, patern: String = "mm:ss"): String =
            SimpleDateFormat(patern, Locale.getDefault()).format(lTime)

        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }
}