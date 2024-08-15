package com.example.playlistmaker.util

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

object FormatUtils {
    fun formatLongToTrakTime(lTime: Long, patern: String = "mm:ss"): String =
        SimpleDateFormat(patern, Locale.getDefault()).format(lTime)

    fun formatLongToTimeMillis(lTime: Long, patern: String = "mm"): String =
        SimpleDateFormat(patern, Locale.getDefault()).format(lTime)

    fun formatIntToTrackCount(iCount: Int, patern: String = "mm"): String =
        SimpleDateFormat(patern, Locale.getDefault()).format(iCount)

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()

    }

    fun formatIsoToYear(isoDateString: String): String =
        SimpleDateFormat("yyyy", Locale.getDefault())
            .format(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    .parse(isoDateString)!!
            )
}