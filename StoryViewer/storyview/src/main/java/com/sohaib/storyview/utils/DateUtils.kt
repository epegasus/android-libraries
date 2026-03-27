package com.sohaib.storyview.utils

import java.util.*
import kotlin.math.abs

/**
 * Created by Sohaib Ahmed on 03/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

object DateUtils {

    fun getDurationBetweenDates(d1: Date, d2: Date): String {
        val diff = d1.time - d2.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        val formattedDiff = java.lang.StringBuilder()
        if (days != 0L) {
            return formattedDiff.append(abs(days).toString() + "d").toString()
        }
        if (hours != 0L) {
            return formattedDiff.append(abs(hours).toString() + "h").toString()
        }
        if (minutes != 0L) {
            return formattedDiff.append(abs(minutes).toString() + "m").toString()
        }
        return if (seconds != 0L) {
            formattedDiff.append(abs(seconds).toString() + "s").toString()
        } else ""
    }
}