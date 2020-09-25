package org.navgurukul.saral.util

import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object DateTimeUtil {
    fun stringToDate(inputDateTime:String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(inputDateTime)
        return outputFormat.format(date)
    }

    fun stringToTime(inputDateTime:String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date: Date = inputFormat.parse(inputDateTime)
        return outputFormat.format(date)
    }

    fun stringToDay(inputDateTime:String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val date: Date = inputFormat.parse(inputDateTime)
        return outputFormat.format(date)
    }


}